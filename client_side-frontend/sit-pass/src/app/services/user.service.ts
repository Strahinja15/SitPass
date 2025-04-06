import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { UserDTO } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userUrl = 'http://localhost:8600/api/user';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getUserProfile(): Observable<any> {
    return this.http.get<any>(`${this.userUrl}/profile`, {
      headers: this.authService.getAuthHeaders()
    });
  }

  getAccountRequests(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.userUrl}/requests?page=${page}&size=${size}`);
  }

  updateUserProfile(profileData: any): Observable<any> {
    return this.http.put<any>(`${this.userUrl}/update`, profileData, {
      headers: this.authService.getAuthHeaders()
    });
  }

  acceptRequest(requestId: number): Observable<void> {
    return this.http.put<void>(`${this.userUrl}/request/${requestId}/approve`, {}, {
      headers: this.authService.getAuthHeaders()
    });
  }

  rejectRequest(requestId: number, reason: string): Observable<void> {
    return this.http.put<void>(`${this.userUrl}/request/${requestId}/reject`, { reason }, {
      headers: this.authService.getAuthHeaders()
    });
  }

  changePassword(oldPassword: string, newPassword: string): Observable<any> {
    const changePasswordDTO = { oldPassword, newPassword, repeatNewPassword: newPassword };
    return this.http.post<any>(`${this.userUrl}/change-password`, changePasswordDTO, {
      headers: this.authService.getAuthHeaders()
    });
  }

  uploadImage(image: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', image);

    const authHeader = this.authService.getAuthHeaders().get('Authorization') || '';
    
    return this.http.post<any>(`${this.userUrl}/upload-image`, formData, {
      headers: new HttpHeaders({
        'Authorization': authHeader
      })
    });
  }

  getAllUsersWithRoleUserAndManager(): Observable<any[]> {
    return this.http.get<any[]>(`${this.userUrl}/users`, {
      headers: this.authService.getAuthHeaders()
    });
  }
  
  getUserByEmail(email: string): Observable<UserDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<UserDTO>(`${this.userUrl}/email/${email}`, { headers });
  }

  getUserById(userId: number): Observable<UserDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<UserDTO>(`${this.userUrl}/${userId}`, { headers });
  }
  
}
