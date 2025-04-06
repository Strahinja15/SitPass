import { Component, OnInit } from '@angular/core';
import { FacilityDTO } from '../../model/facility.model';
import { FacilityService } from '../../services/facility.service';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  facilities: FacilityDTO[] = [];
  users: any[] = [];
  showActivateModal: boolean = false;
  selectedFacilityId!: number;
  selectedUserId!: number;
  startDate: string = '';
  endDate: string = '';

  showCreateModal: boolean = false;
  newFacility: Partial<FacilityDTO> = {
    name: '',
    description: '',
    city: '',
    address: ''
  };

  errorMessage: string | null = null;

  constructor(
    private facilityService: FacilityService,
    private userService: UserService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadFacilities();
  }

  logout(): void {
    this.authService.logout();
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  loadFacilities(): void {
    this.facilityService.getAllFacilities().subscribe(
      facilities => {
        this.facilities = facilities;
      },
      error => {
        console.error('Error loading facilities:', error);
      }
    );
  }

  openActivateModal(facilityId: number): void {
    this.selectedFacilityId = facilityId;
    this.userService.getAllUsersWithRoleUserAndManager().subscribe({
      next: (users) => {
        console.log('Users fetched:', users); // Log the fetched users
        this.users = users;
        this.showActivateModal = true;
      },
      error: (err) => {
        console.error('Error fetching users:', err);
      }
    });
  }

  closeModal(): void {
    this.showActivateModal = false;
  }

  confirmActivation(): void {
    console.log('Selected User ID:', this.selectedUserId); // Log the selected user ID
    if (!this.selectedUserId) {
      console.error('No user selected for activation');
      return;
    }
    const payload = {
      userId: this.selectedUserId,
      startDate: this.startDate,
      endDate: this.endDate
    };
    this.facilityService.activateFacility(this.selectedFacilityId, payload).subscribe({
      next: () => {
        this.loadFacilities();
        this.closeModal();
      },
      error: (err) => {
        console.error('Error activating facility:', err);
      }
    });
  }

  deactivateFacility(facilityId: number): void {
    this.facilityService.deactivateFacility(facilityId).subscribe({
      next: () => this.loadFacilities(),
      error: (err) => console.error('Error deactivating facility:', err)
    });
  }

  openCreateModal(): void {
    this.showCreateModal = true;
  }
  
  closeCreateModal(): void {
    this.showCreateModal = false;
  }
  
  createFacility(): void {
    if (this.newFacility.name && this.newFacility.description && this.newFacility.city && this.newFacility.address) {
      const facility: FacilityDTO = {
        ...this.newFacility,
        id: 0,
        createdAt: new Date().toISOString(),
        rating: 0,
        active: false,
        isDeleted: false,
        userId: null,
        imageIds: [],
        exerciseIds: [],
        disciplinesIds: [],
        workdays: []
      } as FacilityDTO;

      this.facilityService.createFacility(facility).subscribe({
        next: (createdFacility) => {
          this.facilities.push(createdFacility);
          this.newFacility = { name: '', description: '', city: '', address: '' };
          this.closeCreateModal();
        },
        error: (err) => {
          this.errorMessage = err;
          console.error('Error creating facility:', err);
        }
      });
    }
  }
  
  deleteFacility(facilityId: number): void {
    this.facilityService.deleteFacility(facilityId).subscribe({
      next: (deletedFacility) => {
        this.facilities = this.facilities.filter(f => f.id !== facilityId);
      },
      error: (err) => {
        console.error('Error deleting facility:', err);
      }
    });
  }
}
