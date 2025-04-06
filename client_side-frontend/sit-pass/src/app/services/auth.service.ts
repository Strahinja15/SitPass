import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { switchMap, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { UserService } from './user.service';
import { FacilityService } from './facility.service';

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private apiUrl = 'http://localhost:8600/api/authorization';

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string) {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap(response => {
        this.setToken(response.token);
        const decodedToken: any = jwtDecode(response.token);
        const role = decodedToken.role?.authority;
        this.setUserRole(role);
      })
    );
  }

  logout(): void {
    const token = this.getToken();
    if (token) {
      this.http.post(`${this.apiUrl}/logout`, {}, {
        headers: new HttpHeaders({
          'Authorization': `Bearer ${token}`
        })
      }).subscribe({
        next: () => {
          localStorage.removeItem('token');
          localStorage.removeItem('role');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Logout error:', err);
          this.router.navigate(['/login']);
        }
      });
    } else {
      this.router.navigate(['/login']);
    }
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  setUserRole(role: string): void {
    localStorage.setItem('role', role);
  }

  getUserRole(): string | null {
    return localStorage.getItem('role');
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  isAdmin(): boolean {
    return this.getUserRole() === 'ROLE_administrator';
  }

  getEmail(): string | null {
    const token = this.getToken();
    if (token) {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.sub;
    }
    return null;
  }
}
