import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommentDTO } from '../model/comment.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = 'http://localhost:8600/api/comments';

  constructor(private http: HttpClient, private authService: AuthService) {}

  createComment(comment: CommentDTO): Observable<CommentDTO> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<CommentDTO>(`${this.apiUrl}/createComment`, comment, { headers });
  }
}