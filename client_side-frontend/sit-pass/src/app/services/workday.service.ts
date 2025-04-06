import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WorkdayDTO } from '../model/workday.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class WorkdayService {
  private apiUrl = 'http://localhost:8600/api/workdays';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getWorkdaysByFacilityId(facilityId: number): Observable<WorkdayDTO[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<WorkdayDTO[]>(`${this.apiUrl}/facility/${facilityId}`, { headers });
  }

  createWorkday(workday: WorkdayDTO, facilityId: number): Observable<WorkdayDTO> {
    const headers = this.authService.getAuthHeaders();
    const url = `${this.apiUrl}/createWorkday?facilityId=${facilityId}`;
    return this.http.post<WorkdayDTO>(url, workday, { headers });
  }

  deleteWorkday(id: number): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<void>(`${this.apiUrl}/deleteWorkday/${id}`, { headers });
  }
  
  
}
