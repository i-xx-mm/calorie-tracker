import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../shared/services/auth.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { of } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['register']);
    const notifySpy = jasmine.createSpyObj('NotificationService', [
      'success',
      'error',
    ]);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: NotificationService, useValue: notifySpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    notificationService = TestBed.inject(
      NotificationService,
    ) as jasmine.SpyObj<NotificationService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('register form invalid when empty', () => {
    expect(component.registerForm.valid).toBeFalse();
  });

  it('password mismatch triggers form-level mismatch error', () => {
    component.registerForm.setValue({
      username: 'testuser',
      height: 170,
      weight: 70,
      age: 25,
      gender: 'female',
      password: '123456',
      confirmPassword: '654321',
    });
    expect(component.registerForm.hasError('mismatch')).toBeTrue();
  });

  it('password match removes mismatch error', () => {
    component.registerForm.setValue({
      username: 'testuser',
      height: 170,
      weight: 70,
      age: 25,
      gender: 'female',
      password: '123456',
      confirmPassword: '123456',
    });
    expect(component.registerForm.hasError('mismatch')).toBeFalse();
  });

  it('calls authService.register on valid submit, strips confirmPassword', () => {
    authService.register.and.returnValue(
      of({
        token: '',
        username: 'testuser',
        expiresIn: 3600,
      }),
    );

    const formValue = {
      username: 'testuser',
      height: 170,
      weight: 70,
      age: 25,
      gender: 'female',
      password: '123456',
      confirmPassword: '123456',
    };
    component.registerForm.setValue(formValue);
    component.onSubmit();

    expect(authService.register).toHaveBeenCalledWith({
      username: 'testuser',
      height: 170,
      weight: 70,
      age: 25,
      gender: 'female',
      password: '123456',
    });
  });

  it('does not invoke register when form invalid', () => {
    component.registerForm.setValue({
      username: '',
      height: '',
      weight: '',
      age: '',
      gender: '',
      password: '',
      confirmPassword: '',
    });
    component.onSubmit();
    expect(authService.register).not.toHaveBeenCalled();
  });
});
