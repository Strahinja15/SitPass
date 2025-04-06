import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { ExerciseService } from 'src/app/services/exercise.service';
import { FacilityService } from 'src/app/services/facility.service';
import { ReviewService } from 'src/app/services/review.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  userProfile: any = {};
  accountRequests: any[] = [];
  isAdmin: boolean = false;
  isManager: boolean = false;
  showEditModal: boolean = false;
  editProfileData: any = {};
  editProfileErrors: any = {};
  showModal: boolean = false;
  rejectReason: string = '';
  rejectRequestId: number | null = null;
  changePasswordData = {
    oldPassword: '',
    newPassword: '',
    repeatNewPassword: ''
  };
  changePasswordMessage: string | null = null;
  changePasswordSuccess: boolean = false;
  changePasswordErrors: any = {};
  updateMessage: string | null = null;
  updateSuccess: boolean = false;
  currentPage: number = 0;
  totalPages: number = 0;
  pageSize: number = 5;
  exerciseHistory: any[] = [];
  reviews: any[] = [];

  currentPageExercises: number = 0;
  pageSizeExercises: number = 5;
  totalPagesExercises: number = 0;

  currentPageReview: number = 0;
  totalPagesReview: number = 0;
  pageSizeReview: number = 5;
  facilityIds: number[] = [];

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private exerciseService: ExerciseService,
    private facilityService: FacilityService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    const email = this.authService.getEmail();
    if (email) {
      this.userService.getUserByEmail(email).subscribe(user => {
        this.facilityService.getFacilitiesByManager(user.id).subscribe(facilityIds => {
          this.facilityIds = facilityIds;
          this.userService.getUserProfile().subscribe(profile => {
            this.userProfile = profile.profile;
            console.log('User Profile:', this.userProfile);
            this.loadExerciseHistory();
            if (this.isAdmin) {
              this.loadAccountRequests();
              this.loadAllReviews();
            } else {
              this.loadReviewsByManager(this.facilityIds);
            }
          });
        });
      });
    }
  }

  loadExerciseHistory(): void {
    this.userService.getUserByEmail(this.userProfile.email).subscribe(user => {
      this.exerciseService.getExercisesByUserId(user.id, this.currentPageExercises, this.pageSizeExercises).subscribe(response => {
        this.exerciseHistory = response.content;
        this.totalPagesExercises = response.totalPages;
        this.exerciseHistory.forEach(exercise => {
          this.facilityService.getFacilityById(exercise.facilityId).subscribe(facility => {
            exercise.facilityName = facility.name; // Pretpostavimo da FacilityDTO ima polje `name`
          });
        });
      });
    });
  }

  loadAccountRequests(): void {
    this.userService.getAccountRequests(this.currentPage, this.pageSize).subscribe(response => {
      this.accountRequests = response.content;
      this.totalPages = response.totalPages;
    });
  }

  loadAllReviews(): void {
    this.reviewService.getAllReviews(this.currentPageReview, this.pageSizeReview).subscribe(response => {
      this.reviews = response.content;
      this.totalPagesReview = response.totalPages;
    });
  }

  loadReviewsByManager(facilityIds: number[]): void {
    if (facilityIds.length > 0) {
      this.reviewService.getReviewsByFacilityIds(facilityIds, this.currentPageReview, this.pageSizeReview).subscribe(response => {
        this.reviews = response.content;
        this.totalPagesReview = response.totalPages;
        console.log('Reviews:', this.reviews);
      });
    } else {
      console.error('No facility IDs found.');
    }
  }

  

  nextPageReview(): void {
    if (this.currentPageReview < this.totalPagesReview - 1) {
      this.currentPageReview++;
      if (this.isAdmin) {
        this.loadAllReviews();
      } else {
        this.loadReviewsByManager(this.facilityIds);
      }
    }
  }

  previousPageReview(): void {
    if (this.currentPageReview > 0) {
      this.currentPageReview--;
      if (this.isAdmin) {
        this.loadAllReviews();
      } else {
        this.loadReviewsByManager(this.facilityIds);
      }
    }
  }

  openEditModal(): void {
    this.editProfileData = { ...this.userProfile };
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editProfileErrors = {};
  }

  confirmEdit(): void {
    this.editProfileErrors = {};

    if (!this.editProfileData.email) {
      this.editProfileErrors.email = 'Email is required.';
    } else if (!this.isValidEmail(this.editProfileData.email)) {
      this.editProfileErrors.email = 'Email is invalid.';
    }

    if (!this.editProfileData.name) {
      this.editProfileErrors.name = 'Name is required.';
    }

    if (!this.editProfileData.surname) {
      this.editProfileErrors.surname = 'Surname is required.';
    }

    if (!this.editProfileData.phoneNumber) {
      this.editProfileErrors.phoneNumber = 'Phone number is required.';
    }

    if (!this.editProfileData.birthday) {
      this.editProfileErrors.birthday = 'Birthday is required.';
    }

    if (!this.editProfileData.address) {
      this.editProfileErrors.address = 'Address is required.';
    }

    if (!this.editProfileData.city) {
      this.editProfileErrors.city = 'City is required.';
    }

    if (!this.editProfileData.zipCode) {
      this.editProfileErrors.zipCode = 'ZIP/Postal code is required.';
    }

    if (Object.keys(this.editProfileErrors).length > 0) {
      return;
    }

    this.userService.updateUserProfile(this.editProfileData).subscribe(response => {
      this.loadUserProfile();
      this.updateMessage = response.message;
      this.updateSuccess = true;
      this.closeEditModal();
    });
  }

  isValidEmail(email: string): boolean {
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@(([^<>()[\]\\.,;:\s@"]+\.)+[^<>()[\]\\.,;:\s@"]{2,})$/i;
    return re.test(String(email).toLowerCase());
  }

  logout(): void {
    this.authService.logout();
  }

  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  goToHomePage(): void {
    this.router.navigate(['/homepage']);
  }

  acceptRequest(requestId: number): void {
    this.userService.acceptRequest(requestId).subscribe(() => {
      this.loadAccountRequests();
    });
  }

  openRejectModal(requestId: number): void {
    this.rejectRequestId = requestId;
    this.showModal = true;
  }

  closeRejectModal(): void {
    this.showModal = false;
    this.rejectReason = '';
    this.rejectRequestId = null;
  }

  confirmReject(): void {
    if (this.rejectRequestId !== null) {
      this.userService.rejectRequest(this.rejectRequestId, this.rejectReason).subscribe(() => {
        this.loadAccountRequests();
        this.closeRejectModal();
      });
    }
  }

  changePassword(): void {
    const { oldPassword, newPassword, repeatNewPassword } = this.changePasswordData;
    this.changePasswordErrors = {};

    if (!oldPassword) {
      this.changePasswordErrors.oldPassword = 'Old password is required.';
    }
    if (!newPassword) {
      this.changePasswordErrors.newPassword = 'New password is required.';
    }
    if (!repeatNewPassword) {
      this.changePasswordErrors.repeatNewPassword = 'Repeat new password is required.';
    }
    if (newPassword !== repeatNewPassword) {
      this.changePasswordErrors.repeatNewPassword = 'New passwords do not match.';
    }

    if (Object.keys(this.changePasswordErrors).length > 0) {
      return;
    }

    this.userService.changePassword(oldPassword, newPassword).subscribe(response => {
      this.loadUserProfile();
      this.changePasswordMessage = response.message;
      this.changePasswordSuccess = true;
      this.changePasswordData = { oldPassword: '', newPassword: '', repeatNewPassword: '' };
    });
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadAccountRequests();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadAccountRequests();
    }
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.userService.uploadImage(file).subscribe({
        next: () => {
          this.loadUserProfile();
        },
        error: err => {
          console.error('Error uploading image', err);
        }
      });
    }
  }

  nextPageExercises(): void {
    if (this.currentPageExercises < this.totalPagesExercises - 1) {
      this.currentPageExercises++;
      this.loadExerciseHistory();
    }
  }

  previousPageExercises(): void {
    if (this.currentPageExercises > 0) {
      this.currentPageExercises--;
      this.loadExerciseHistory();
    }
  }
}
