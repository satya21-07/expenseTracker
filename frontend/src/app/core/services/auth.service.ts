import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080/api/auth';
    private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());

    constructor(private http: HttpClient, private router: Router) { }

    public get isAuthenticated$(): Observable<boolean> {
        return this.isAuthenticatedSubject.asObservable();
    }

    private hasToken(): boolean {
        return !!localStorage.getItem('token');
    }

    login(credentials: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
            tap((res: any) => {
                localStorage.setItem('token', res.token);
                this.isAuthenticatedSubject.next(true);
            })
        );
    }

    register(userData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/register`, userData).pipe(
            tap((res: any) => {
                localStorage.setItem('token', res.token);
                this.isAuthenticatedSubject.next(true);
            })
        );
    }

    logout() {
        localStorage.removeItem('token');
        this.isAuthenticatedSubject.next(false);
        this.router.navigate(['/login']);
    }

    getToken(): string | null {
        return localStorage.getItem('token');
    }
}
