import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RateDTO } from '../model/rate.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RateService {
  private apiUrl = 'http://localhost:8600/api/rates';

  constructor(private http: HttpClient, private authService: AuthService) {}

  createRate(rate: RateDTO): Observable<RateDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<RateDTO>(`${this.apiUrl}/createRate`, rate, { headers });
  }
}