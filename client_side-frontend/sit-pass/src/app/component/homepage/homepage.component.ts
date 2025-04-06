// homepage.component.ts
import { Component, OnInit } from '@angular/core';
import { FacilityService } from '../../services/facility.service';
import { FacilityDTO } from '../../model/facility.model';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ImageDTO } from 'src/app/model/image.model';
import { ImageService } from 'src/app/services/image.service';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.scss']
})
export class HomepageComponent implements OnInit {
  facilities: FacilityDTO[] = [];
  facilityImages: { [facilityId: number]: string } = {};
  cityFilter: string = '';
  disciplineFilter: string = '';
  minRating: number | null = null;
  maxRating: number | null = null;
  workdayFilter: string = '';

  constructor(private facilityService: FacilityService, public authService: AuthService, private imageService: ImageService, private router: Router) {}

  ngOnInit(): void {
    this.loadFacilities();
  }

  loadFacilities(): void {
    this.facilityService.getAllFacilities().subscribe((data: FacilityDTO[]) => {
      this.facilities = data;
      this.loadFacilityImages();
    });
  }

  loadFacilityImages(): void {
    this.facilities.forEach(facility => {
      if (facility.imageIds && facility.imageIds.length > 0) {
        this.imageService.getImagesByFacilityId(facility.id).subscribe((images: ImageDTO[]) => {
          if (images.length > 0) {
            this.facilityImages[facility.id] = this.getImageUrl(images[0].path);
          }
        });
      }
    });
  }

  getImageUrl(imagePath: string): string {
    return `http://localhost:8600/images/${imagePath}`;
  }

  filterFacilities(): void {
    let ratingRange: [number, number] | undefined = undefined;
    
    if (this.minRating !== null && this.maxRating !== null) {
      ratingRange = [this.minRating, this.maxRating];
    }
    
    this.facilityService.filterFacilities(this.cityFilter, this.disciplineFilter, ratingRange, this.workdayFilter).subscribe((data: FacilityDTO[]) => {
      this.facilities = data;
      this.loadFacilityImages();
    });
  }  

  logout(): void {
    this.authService.logout();
  }

  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  viewFacility(facilityId: number): void {
    this.router.navigate(['/facility', facilityId]);
  }
}
