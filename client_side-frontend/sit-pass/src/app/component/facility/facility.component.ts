import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FacilityService } from 'src/app/services/facility.service';
import { FacilityDTO } from 'src/app/model/facility.model';
import { ImageService } from 'src/app/services/image.service';
import { ExerciseService } from 'src/app/services/exercise.service';
import { DisciplineService } from 'src/app/services/discipline.service';
import { WorkdayService } from 'src/app/services/workday.service';
import { ImageDTO } from 'src/app/model/image.model';
import { DisciplineDTO } from 'src/app/model/discipline.model';
import { WorkdayDTO } from 'src/app/model/workday.model';
import { AuthService } from 'src/app/services/auth.service'; 
import { Router } from '@angular/router';
import { UserService } from 'src/app/services/user.service';
import { UserDTO } from 'src/app/model/user.model';
import { ExerciseDTO } from 'src/app/model/exercise.model';
import { DaysOfWeek } from 'src/app/model/daysofweek.model';
import { ReviewService } from 'src/app/services/review.service';
import { RateService } from 'src/app/services/rate.service';
import { CommentService } from 'src/app/services/comment.service';
import { ReviewDTO } from 'src/app/model/review.model';
import { RateDTO } from 'src/app/model/rate.model';
import { CommentDTO } from 'src/app/model/comment.model';
import { Page } from 'src/app/model/page.model';

@Component({
  selector: 'app-facility',
  templateUrl: './facility.component.html',
  styleUrls: ['./facility.component.scss']
})
export class FacilityComponent implements OnInit {
  facility?: FacilityDTO;
  images: ImageDTO[] = [];
  disciplines: DisciplineDTO[] = [];
  workdays: WorkdayDTO[] = [];
  reviews: ReviewDTO[] = [];
  userNames: { [key: number]: string } = {};
  currentImageIndex: number = 0;
  showEditModal: boolean = false;
  editFacility: Partial<FacilityDTO> = {};
  showDisciplineModal: boolean = false;
  newDisciplineName: string = '';
  showWorkdayModal: boolean = false;
  newWorkday: Partial<WorkdayDTO> = {};
  showImageModal: boolean = false;
  selectedFile: File | null = null;
  isManagerOfFacility: boolean = false;
  showReserveModal: boolean = false;
  reserveExercise: any = {
    startDate: '',
    startTime: '',
    endTime: ''
  };
  errorMessage: string = '';
  isReserveValid: boolean = false;
  isSubmittingReview: boolean = false;

  showReviewModal: boolean = false;
  reviewDTO: ReviewDTO = {
    id: 0,
    createdAt: new Date(),
    exerciseCount: 0,
    hidden: false,
    userId: 0,
    facilityId: 0,
    comments: [],
    rateDTO: {
      id: 0,
      equipment: 1,
      staff: 1,
      hygiene: 1,
      space: 1,
      reviewId: 0
    }
  };
  rateDTO: RateDTO = {
    id: 0,
    equipment: 1,
    staff: 1,
    hygiene: 1,
    space: 1,
    reviewId: 0
  };
  commentDTO: CommentDTO = {
    id: 0,
    text: '',
    createdAt: new Date(),
    reviewId: 0,
    userId: 0
  };
  exerciseCount: number = 0;

  averageRatings: { [key: string]: number } = {};
  reviewCount: number = 0;

  constructor(
    private route: ActivatedRoute,
    private facilityService: FacilityService,
    private imageService: ImageService,
    private disciplineService: DisciplineService,
    private workdayService: WorkdayService,
    public authService: AuthService,
    private router: Router,
    private userService: UserService,
    private exerciseService: ExerciseService,
    private reviewService: ReviewService,
    private rateService: RateService,
    private commentService: CommentService
  ) {}

  ngOnInit(): void {
    this.loadFacilityData();
    
  }

  loadFacilityData(): void {
    const facilityIdParam = this.route.snapshot.paramMap.get('id');
    const email = this.authService.getEmail();
    if (email && facilityIdParam) {
      const facilityId = +facilityIdParam;
      this.facilityService.getFacilityById(facilityId).subscribe((data: FacilityDTO) => {
        this.facility = data;
        console.log('Facility loaded:', this.facility);
        this.loadImages(facilityId);
        this.loadDisciplines(facilityId);
        this.loadWorkdays(facilityId);
        this.checkManagerStatus(facilityId, email);
        this.loadAverageRatings(facilityId);
        this.loadReviewCount(facilityId);
        this.loadReviews(facilityId);
      });
    } else {
      console.error('Facility ID not found in the URL or user is not authenticated');
    }
  }

  logout(): void {
    this.authService.logout();
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  loadUserName(userId: number): void {
    if (!this.userNames[userId]) {  
      this.userService.getUserById(userId).subscribe({
        next: (user: UserDTO) => {
          this.userNames[userId] = user.name; 
        },
        error: (err) => {
          console.error('Error loading user:', err);
        }
      });
    }
  }
  
  calculateAverageRating(rate: RateDTO): number {
    return (rate.equipment + rate.staff + rate.hygiene + rate.space) / 4;
  }

  hideComment(reviewId: number): void {
    this.reviewService.hideReview(reviewId).subscribe({
      next: () => {
        const review = this.reviews.find(r => r.id === reviewId);
        if (review) {
          review.hidden = true;
        }
      },
      error: (err) => {
        console.error('Error hiding review:', err);
      }
    });
  }

  deleteComment(reviewId: number): void {
    this.reviewService.deleteReview(reviewId).subscribe({
      next: () => {
        this.reviews = this.reviews.filter(r => r.id !== reviewId);
        this.loadFacilityData();
      },
      error: (err) => {
        console.error('Error deleting review:', err);
      }
    });
  }
  

  loadReviews(facilityId: number): void {
    const page = 0;
    const size = 5;
    this.reviewService.getReviewsByFacilityId(facilityId, page, size).subscribe({
      next: (data: Page<ReviewDTO>) => {
        console.log('Reviews loaded:', data);
        this.reviews = data.content;
  
        
        this.reviews.forEach(review => {
          this.loadUserName(review.userId);
        });
      },
      error: (err) => {
        console.error('Error loading reviews:', err);
      }
    });
  }
  

  loadImages(facilityId: number): void {
    this.imageService.getImagesByFacilityId(facilityId).subscribe((data: ImageDTO[]) => {
      this.images = data;
    });
  }

  loadDisciplines(facilityId: number): void {
    this.disciplineService.getDisciplinesByFacilityId(facilityId).subscribe((data: DisciplineDTO[]) => {
      this.disciplines = data.filter(discipline => discipline && discipline.name);
    });
  }

  loadWorkdays(facilityId: number): void {
    this.workdayService.getWorkdaysByFacilityId(facilityId).subscribe((data: WorkdayDTO[]) => {
      this.workdays = data;
    });
  }

  loadAverageRatings(facilityId: number): void {
    this.reviewService.getAverageRatingsForFacility(facilityId).subscribe((averageRatings: { [key: string]: number }) => {
      this.averageRatings = averageRatings;
    });
  }

  loadReviewCount(facilityId: number): void {
    this.reviewService.getReviewsForFacility(facilityId).subscribe((reviews: ReviewDTO[]) => {
      this.reviewCount = reviews.length;
    });
  }

  getImageUrl(imagePath: string): string {
    return `http://localhost:8600/images/${imagePath}`;
  }

  prevImage(): void {
    if (this.currentImageIndex > 0) {
      this.currentImageIndex--;
    }
  }

  nextImage(): void {
    if (this.currentImageIndex < this.images.length - 1) {
      this.currentImageIndex++;
    }
  }

  isArrayNonEmpty(arr: any[] | null | undefined): boolean {
    return Array.isArray(arr) && arr.length > 0;
  }

  getFormattedWorkdays(): string {
    return this.workdays.map(w => w.days + ': ' + w.from + ' - ' + w.until).join(', ');
  }

  getFormattedDisciplines(): string {
    return this.disciplines
      .filter(d => d && d.name)
      .map(d => d.name)
      .join(', ');
  }

  openEditModal(): void {
    this.showEditModal = true;
    this.editFacility = { ...this.facility };
  }

  closeEditModal(): void {
    this.showEditModal = false;
  }

  saveEdit(): void {
    if (this.facility && this.editFacility) {
      this.facilityService.updateFacility(this.facility.id, this.editFacility as FacilityDTO).subscribe({
        next: (updatedFacility) => {
          this.facility = updatedFacility;
          this.closeEditModal();
        },
        error: (err) => {
          console.error('Error updating facility:', err);
        }
      });
    }
  }

  openDisciplineModal(): void {
    this.showDisciplineModal = true;
  }

  closeDisciplineModal(): void {
    this.showDisciplineModal = false;
  }

  addDiscipline(): void {
    if (this.facility && this.newDisciplineName.trim()) {
      const newDiscipline: DisciplineDTO = {
        id: 0,
        name: this.newDisciplineName,
        facilityId: this.facility.id
      };

      this.disciplineService.createDiscipline(newDiscipline, this.facility.id).subscribe({
        next: (createdDiscipline) => {
          this.disciplines.push(createdDiscipline);
          this.newDisciplineName = '';
        },
        error: (err) => {
          console.error('Error creating discipline:', err);
        }
      });
    }
  }

  deleteDiscipline(disciplineId: number): void {
    this.disciplineService.deleteDiscipline(disciplineId).subscribe({
      next: () => {
        this.disciplines = this.disciplines.filter(d => d.id !== disciplineId);
      },
      error: (err) => {
        console.error('Error deleting discipline:', err);
      }
    });
  }

  openWorkdayModal(): void {
    this.showWorkdayModal = true;
  }

  closeWorkdayModal(): void {
    this.showWorkdayModal = false;
  }

  addWorkday(): void {
    if (this.facility && this.newWorkday.days && this.newWorkday.from && this.newWorkday.until) {
        if (this.isDayAlreadyAssigned(this.newWorkday.days)) {
            console.error('Workday for this day already exists for this facility.');
            this.errorMessage = 'Workday for ' + this.newWorkday.days + ' already exists for this facility.';
            return;
        }
        const newWorkday: WorkdayDTO = {
            id: 0,
            validFrom: new Date(), 
            days: this.newWorkday.days,
            from: this.newWorkday.from,
            until: this.newWorkday.until,
            facilityId: this.facility.id
        };
        this.workdayService.createWorkday(newWorkday, this.facility.id).subscribe({
            next: (createdWorkday) => {
                this.workdays.push(createdWorkday);
                this.newWorkday = {};  
                this.errorMessage = ''; 
            },
            error: (err) => {
                console.error('Error creating workday:', err);
                this.errorMessage = 'Error creating workday: ' + (err.error.message || 'Unknown error');
            }
        });
    } else {
        this.errorMessage = 'Please fill in all fields.';
    }
  }

  deleteWorkday(workdayId: number): void {
    this.workdayService.deleteWorkday(workdayId).subscribe({
      next: () => {
        this.workdays = this.workdays.filter(w => w.id !== workdayId);
      },
      error: (err) => {
        console.error('Error deleting workday:', err);
      }
    });
  }

  isDayAlreadyAssigned(day: string): boolean {
    return this.workdays.some(workday => workday.days.toLowerCase() === day.toLowerCase());
  }

  openImageModal(): void {
    this.showImageModal = true;
  }

  closeImageModal(): void {
    this.showImageModal = false;
    this.selectedFile = null;
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  uploadImage(): void {
    if (this.selectedFile && this.facility) {
      const formData = new FormData();
      formData.append('file', this.selectedFile);

      this.imageService.uploadImage(this.facility.id, formData).subscribe({
        next: (newImage) => {
          this.images.push(newImage);
          this.closeImageModal();
        },
        error: (err) => {
          console.error('Error uploading image:', err);
        }
      });
    }
  }

  deleteImage(imageId: number): void {
    this.imageService.deleteImage(imageId).subscribe({
      next: () => {
        this.images = this.images.filter(img => img.id !== imageId);
      },
      error: (err) => {
        console.error('Error deleting image:', err);
      }
    });
  }

  openReserveModal(): void {
    this.showReserveModal = true;
  }

  closeReserveModal(): void {
    this.showReserveModal = false;
    this.reserveExercise = {};
    this.errorMessage = '';
    this.isReserveValid = false;
  }

  validateDate(): void {
    const selectedDate = new Date(this.reserveExercise.startDate!);
    const today = new Date();
    if (selectedDate < today) {
      this.errorMessage = 'Date cannot be in the past';
      this.isReserveValid = false;
    } else {
      this.errorMessage = '';
      this.isReserveValid = true;
    }
  }

  validateTime(): void {
    const selectedDay = new Date(this.reserveExercise.startDate!).getDay();
    const workday = this.workdays.find(w => DaysOfWeek[selectedDay] === w.days);

    if (workday) {
      const from = new Date(`1970-01-01T${workday.from}:00Z`);
      const until = new Date(`1970-01-01T${workday.until}:00Z`);
      const startTime = new Date(`1970-01-01T${this.reserveExercise.from}:00Z`);
      const endTime = new Date(`1970-01-01T${this.reserveExercise.until}:00Z`);

      if (startTime < from || endTime > until || startTime >= endTime) {
        this.errorMessage = 'Time must be within working hours and valid';
        this.isReserveValid = false;
      } else {
        this.errorMessage = '';
        this.isReserveValid = true;
      }
    } else {
      this.errorMessage = 'Selected date is not a workday';
      this.isReserveValid = false;
    }
  }

  confirmReserve(): void {
    if (this.facility) {
      const email = this.authService.getEmail();
      if (email) {
        this.userService.getUserByEmail(email).subscribe(user => {
          const startDate = this.reserveExercise.startDate;
          const fromTime = this.reserveExercise.startTime;
          const untilTime = this.reserveExercise.endTime;

          this.reserveExercise.userId = user.id;
          this.reserveExercise.facilityId = this.facility!.id;
          this.reserveExercise.from = fromTime;
          this.reserveExercise.until = untilTime;

          console.log('Request payload:', this.reserveExercise);

          this.exerciseService.reserveExercise(this.reserveExercise as ExerciseDTO).subscribe({
            next: (exercise) => {
              console.log('Exercise reserved:', exercise);
              this.closeReserveModal();
            },
            error: (err) => {
              console.error('Error reserving exercise:', err);
            }
          });
        });
      }
    }
  }

  openReviewModal(): void {
    const email = this.authService.getEmail();
    if (email && this.facility) {
        this.userService.getUserByEmail(email).subscribe(user => {
            this.exerciseService.getAllExercisesByUserId(user.id).subscribe(exercises => {
                this.exerciseCount = exercises.length;
                if (this.exerciseCount > 0) {
                    this.reviewDTO.exerciseCount = this.exerciseCount;
                    this.reviewDTO.userId = user.id;
                    this.reviewDTO.facilityId = this.facility!.id;

                    this.reviewService.getReviewByUserIdAndFacilityId(user.id, this.facility!.id).subscribe({
                        next: (review: ReviewDTO) => {
                            this.showReviewModal = false;
                            alert('You have already reviewed this facility.');
                        },
                        error: () => {
                            this.showReviewModal = true;
                        }
                    });
                } else {
                    alert('You must have at least one exercise to leave a review.');
                }
            });
        });
      }
    }


  closeReviewModal(): void {
    this.showReviewModal = false;
  }

  submitReview(): void {
    if (!this.commentDTO.text.trim()) {
        alert('Comment cannot be empty.');
        return;
    }

    // Postarajte se da se zahtev šalje samo jednom
    if (this.isSubmittingReview) {
        return;
    }

    this.isSubmittingReview = true;

    this.commentDTO.userId = this.reviewDTO.userId;
    this.reviewDTO.comments = [this.commentDTO];
    this.reviewDTO.rateDTO = this.rateDTO;

    this.reviewService.createReview(this.reviewDTO).subscribe({
        next: (review: ReviewDTO) => {
            this.isSubmittingReview = false; // Resetovanje flag-a
            this.closeReviewModal();
            alert('Review submitted successfully.');
            this.loadFacilityData(); // Osvežavanje podataka nakon slanja pregleda
        },
        error: (err: any) => {
            this.isSubmittingReview = false; // Resetovanje flag-a u slučaju greške
            console.error('Error submitting review:', err);
        }
    });
}

  checkManagerStatus(facilityId: number, email: string): void {
    this.userService.getUserByEmail(email).subscribe(user => {
      this.facilityService.isManagerOfFacility(facilityId, user.id).subscribe(isManager => {
        this.isManagerOfFacility = isManager;
      });
    });
  }
}
