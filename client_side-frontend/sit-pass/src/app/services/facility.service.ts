import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { FacilityDTO } from '../model/facility.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class FacilityService {
  private apiUrl = 'http://localhost:8600/api/facility';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getAllFacilities(): Observable<FacilityDTO[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<FacilityDTO[]>(`${this.apiUrl}/all`, { headers });
  }

  filterFacilities(city?: string, discipline?: string, ratingRange?: [number, number], workday?: string): Observable<FacilityDTO[]> {
    let params: any = {};
    if (city) {
      params.city = city;
    }
    if (discipline) {
      params.discipline = discipline;
    }
    if (ratingRange) {
      params.minRating = ratingRange[0];
      params.maxRating = ratingRange[1];
    }
    if (workday) {
      params.workday = workday;
    }
    const headers = this.authService.getAuthHeaders();
    return this.http.get<FacilityDTO[]>(`${this.apiUrl}/filter`, { headers, params });
  }
  

  getFacilityById(id: number): Observable<FacilityDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<FacilityDTO>(`${this.apiUrl}/${id}`, { headers });
  }

  activateFacility(facilityId: number, payload: { userId: number, startDate: string, endDate: string }): Observable<FacilityDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<FacilityDTO>(`${this.apiUrl}/activate/${facilityId}`, payload, { headers });
  }

  deactivateFacility(facilityId: number): Observable<FacilityDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<FacilityDTO>(`${this.apiUrl}/deactivate/${facilityId}`, {}, { headers });
  }

  updateFacility(id: number, facilityDTO: FacilityDTO): Observable<FacilityDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<FacilityDTO>(`${this.apiUrl}/update/${id}`, facilityDTO, { headers });
  }

  createFacility(facilityDTO: FacilityDTO): Observable<FacilityDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<FacilityDTO>(`${this.apiUrl}/add-facility`, facilityDTO, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }
  
  deleteFacility(id: number): Observable<FacilityDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<FacilityDTO>(`${this.apiUrl}/delete/${id}`, { headers });
  }

  isManagerOfFacility(facilityId: number, userId: number): Observable<boolean> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<boolean>(`${this.apiUrl}/isManagerOfFacility/${facilityId}/${userId}`, { headers });
  }

  getFacilitiesByManager(userId: number): Observable<number[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<number[]>(`${this.apiUrl}/facilitiesByManager/${userId}`, { headers });
  }
  
  private handleError(error: any): Observable<never> {
    let errorMessage = 'An unknown error occurred!';
    if (error.error && typeof error.error === 'string') {
        errorMessage = error.error;
    } else if (error.message) {
        errorMessage = error.message;
    }
    return throwError(() => new Error(errorMessage));
}

  
}
