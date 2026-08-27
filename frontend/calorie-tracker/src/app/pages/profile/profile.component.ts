import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { concatMap, takeUntil } from 'rxjs/operators';
import { User } from '../../shared/models/user.model';
import { AuthService } from '../../shared/services/auth.service';
import { NotificationService } from '../../shared/services/notification.service';
import { UserService } from '../../shared/services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit, OnDestroy {
  profileForm!: FormGroup;
  currentUser: User | null = null;
  loading = true;
  saving = false;

  bmiValue: number | null = null;
  bmiCategory: string = '';

  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private notificationService: NotificationService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadUserProfile();
  }

  ngOnDestroy(): void {
    this.loading = false;
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Initialize form with default empty values
  initializeForm(): void {
    this.profileForm = this.formBuilder.group({
      username: [{ value: '', disabled: true }],
      height: [
        null,
        [Validators.required, Validators.min(50), Validators.max(300)],
      ],
      weight: [
        null,
        [Validators.required, Validators.min(20), Validators.max(500)],
      ],
      age: [null, [Validators.required, Validators.min(13), Validators.max(120)]],
      gender: ['', Validators.required],
    });
  }

  loadUserProfile(): void {
    this.loading = true;
    this.userService
      .getCurrentUser()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (user) => {
          if (!user) {
            this.notificationService.error('User data not available');
            this.loading = false;
            return;
          }
          this.currentUser = user;
          this.updateFormWithUser(user);

          const username = this.authService.getCurrentUsername();
          this.userService
            .getUserBMI(username)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: (res) => {
                this.bmiValue = res.bmi;
                this.bmiCategory = res.category;
              },
              error: () => {
                this.bmiValue = null;
                this.bmiCategory = '';
              },
            });

          this.loading = false;
        },
        error: () => {
          this.notificationService.error('Failed to load profile');
          this.loading = false;
        },
      });
  }

  // Update form with user data (after initialization)
  updateFormWithUser(user: User): void {
    this.profileForm.patchValue({
      username: user.username || '',
      height: user.height || '',
      weight: user.currentWeight || '',
      age: user.age || '',
      gender: user.gender || '',
    });
  }

  getBMIStatusClass(status: string): string {
    switch (status) {
      case 'Underweight':
        return 'status-blue';
      case 'Normal Weight':
        return 'status-green';
      case 'Overweight':
        return 'status-orange';
      case 'Obese':
        return 'status-red';
      default:
        return '';
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.notificationService.error(
        'Please fill in all required fields correctly',
      );
      return;
    }

    this.saving = true;
    const payload = {
      height: this.profileForm.get('height')?.value,
      currentWeight: this.profileForm.get('weight')?.value,
      age: this.profileForm.get('age')?.value,
      gender: this.profileForm.get('gender')?.value,
    };

    const username = this.authService.getCurrentUsername();
    this.userService
      .updateProfile(username, payload)
      .pipe(
        concatMap((updatedUser) => {
          this.currentUser = updatedUser;
          return this.userService.getUserBMI(username);
        }),
        takeUntil(this.destroy$),
      )
      .subscribe({
        next: (bmiRes) => {
          this.bmiValue = bmiRes.bmi;
          this.bmiCategory = bmiRes.category;
          this.notificationService.success('Profile updated successfully');
          this.saving = false;
        },
        error: (err) => {
          this.saving = false;
          this.notificationService.error(
            err.error?.message || 'Failed to update profile',
          );
        },
      });
  }

  get f() {
    return this.profileForm.controls;
  }
}
