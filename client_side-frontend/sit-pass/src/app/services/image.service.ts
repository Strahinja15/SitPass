import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ImageDTO } from '../model/image.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  private apiUrl = 'http://localhost:8600/api/images';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getImagesByFacilityId(facilityId: number): Observable<ImageDTO[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<ImageDTO[]>(`${this.apiUrl}/facility/${facilityId}`, { headers });
  }

  uploadImage(facilityId: number, formData: FormData): Observable<ImageDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<ImageDTO>(`${this.apiUrl}/upload/${facilityId}`, formData, { headers });
  }

  deleteImage(imageId: number): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<void>(`${this.apiUrl}/delete/${imageId}`, { headers });
  }
}
