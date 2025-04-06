import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReviewDTO } from '../model/review.model';
import { AuthService } from './auth.service';
import { Page } from '../model/page.model';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = 'http://localhost:8600/api/reviews';

  constructor(private http: HttpClient, private authService: AuthService) {}

  createReview(review: ReviewDTO): Observable<ReviewDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<ReviewDTO>(`${this.apiUrl}/createReview`, review, { headers });
  }

  getReviewByUserIdAndFacilityId(userId: number, facilityId: number): Observable<ReviewDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<ReviewDTO>(`${this.apiUrl}/user/${userId}/facility/${facilityId}`, { headers });
  }

  getAverageRatingsForFacility(facilityId: number): Observable<{ [key: string]: number }> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<{ [key: string]: number }>(`${this.apiUrl}/${facilityId}/average-ratings`, { headers });
  }

  getReviewsForFacility(facilityId: number): Observable<ReviewDTO[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<ReviewDTO[]>(`${this.apiUrl}/facility/${facilityId}`, { headers });
  }

  getAllReviews(page: number, size: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/all`, { headers, params });
  }

  getReviewsByFacilityIds(facilityIds: number[], page: number, size: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    let params = new HttpParams().set('facilityIds', facilityIds.join(',')).set('page', page.toString()).set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/facility/reviews`, { headers, params });
  }

  getReviewsByFacilityId(facilityId: number, page: number, size: number): Observable<Page<ReviewDTO>> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Page<ReviewDTO>>(`${this.apiUrl}/allById/${facilityId}?page=${page}&size=${size}`, { headers });
  }

  hideReview(reviewId: number): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<void>(`${this.apiUrl}/${reviewId}/hide`, {}, { headers });
  }

  deleteReview(reviewId: number): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<void>(`${this.apiUrl}/${reviewId}/delete`, { headers });
  }
}
