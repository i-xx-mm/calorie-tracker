import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../shared/services/auth.service';
import { NotificationService } from '../../../shared/services/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm!: FormGroup;
  loading = false;

  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  ngOnDestroy(): void {
    this.loading = false;
    this.destroy$.next();
    this.destroy$.complete();
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.authService
      .login(this.loginForm.value)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (resp) => {
          this.loading = false;

          localStorage.setItem(environment.jwtTokenKey, resp.token);
          const user = {
            username: resp.username,
            height: 0,
            currentWeight: 0,
            age: 0,
            gender: 'other',
          };
          localStorage.setItem('currentUser', JSON.stringify(user));
          this.authService.reloadUserFromStorage();
          this.notificationService.success('Login successful');
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.loading = false;
          this.notificationService.error(
            error.error?.message || 'Login failed',
          );
        },
      });
  }
}
