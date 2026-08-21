import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../shared/services/auth.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { environment } from '../../../../environments/environment'

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: (resp) => {
        this.loading = false;

        localStorage.setItem(environment.jwtTokenKey, resp.token);
        const user = {
          username: resp.username,
          height: 0,
          currentWeight: 0,
          age: 0,
          gender: 'other'
        };
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.authService.reloadUserFromStorage();
        this.notificationService.success('Login successful');
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 0);
      },
      error: (error) => {
        this.loading = false;
        this.notificationService.error(error.error?.message || 'Login failed');
      }
    });
  }
}