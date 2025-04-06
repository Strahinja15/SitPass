import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DisciplineDTO } from '../model/discipline.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class DisciplineService {
  private apiUrl = 'http://localhost:8600/api/disciplines';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getDisciplinesByFacilityId(facilityId: number): Observable<DisciplineDTO[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<DisciplineDTO[]>(`${this.apiUrl}/facility/${facilityId}`, { headers });
  }

  createDiscipline(discipline: DisciplineDTO, facilityId: number): Observable<DisciplineDTO> {
    const headers = this.authService.getAuthHeaders();
    const url = `${this.apiUrl}/createDiscipline?facilityId=${facilityId}`;
    return this.http.post<DisciplineDTO>(url, discipline, { headers });
  }
  

  deleteDiscipline(disciplineId: number): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<void>(`${this.apiUrl}/deleteDiscipline/${disciplineId}`, { headers });
  }
}
