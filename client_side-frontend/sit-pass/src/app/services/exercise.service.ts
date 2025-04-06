import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ExerciseDTO } from '../model/exercise.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ExerciseService {
  private apiUrl = 'http://localhost:8600/api/exercises';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getExercisesByFacilityId(facilityId: number): Observable<ExerciseDTO[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<ExerciseDTO[]>(`${this.apiUrl}/facility/${facilityId}`, { headers });
  }

  reserveExercise(exerciseRequest: any): Observable<ExerciseDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<ExerciseDTO>(`${this.apiUrl}/reserve`, exerciseRequest, { headers });
  }

  getExercisesByUserId(userId: number, page: number = 0, size: number = 5): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<any>(`${this.apiUrl}/user/${userId}?page=${page}&size=${size}`, { headers });
  }

  getAllExercisesByUserId(userId: number): Observable<ExerciseDTO[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<ExerciseDTO[]>(`${this.apiUrl}/user/${userId}/all`, { headers });
  }

}
