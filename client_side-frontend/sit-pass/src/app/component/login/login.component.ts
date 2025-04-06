import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.authService.login(this.email, this.password)
      .pipe(
        catchError(error => {
          this.errorMessage = 'Invalid email or password';
          return of(null);
        })
      )
      .subscribe(response => {
        if (response) {
          this.authService.setToken(response.token);
          this.router.navigate(['/homepage']); 
        }
      });
  }

  register(): void {
    this.router.navigate(['/register']);
  }
}
