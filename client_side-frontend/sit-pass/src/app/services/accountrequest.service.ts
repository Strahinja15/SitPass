import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class AccountRequestService {
    private apiUrl = 'http://localhost:8600/api/account-requests/register'; 

    constructor(private http: HttpClient) { }

    register(accountRequest: any): Observable<any> {
        return this.http.post<any>(this.apiUrl, accountRequest)
            .pipe(
                catchError(this.handleError)
            );
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
