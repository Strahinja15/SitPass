import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountRequestService } from '../../services/accountrequest.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
    registerForm: FormGroup;
    errorMessage: string | null = null;

    constructor(
        private fb: FormBuilder, 
        private accountRequestService: AccountRequestService,
        private router: Router
    ) {
        this.registerForm = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            address: ['', Validators.required]
        });
    }

    onSubmit() {
        if (this.registerForm.valid) {
            this.accountRequestService.register(this.registerForm.value).subscribe({
                next: (response) => {
                    console.log('Registration successful', response);
                    this.router.navigate(['/login']);
                },
                error: (error) => {
                    this.errorMessage = error;
                    console.error('Registration failed', error);
                }
            });
        }
    }

    login(): void {
        this.router.navigate(['/login']);
    }
}
