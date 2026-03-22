import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category, Transaction, DashboardStats, Budget, Alert } from '../models/app.models';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private apiUrl = 'http://localhost:8080/api';

    constructor(private http: HttpClient) { }

    // Transactions
    getTransactions(params: any): Observable<any> {
        return this.http.get(`${this.apiUrl}/transactions`, { params });
    }

    addTransaction(transaction: Transaction): Observable<Transaction> {
        return this.http.post<Transaction>(`${this.apiUrl}/transactions`, transaction);
    }

    updateTransaction(id: string, transaction: Transaction): Observable<Transaction> {
        return this.http.put<Transaction>(`${this.apiUrl}/transactions/${id}`, transaction);
    }

    deleteTransaction(id: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/transactions/${id}`);
    }

    // Categories
    getCategories(): Observable<Category[]> {
        return this.http.get<Category[]>(`${this.apiUrl}/categories`);
    }

    addCategory(category: Category): Observable<Category> {
        return this.http.post<Category>(`${this.apiUrl}/categories`, category);
    }

    // Budgets
    getBudgets(): Observable<Budget[]> {
        return this.http.get<Budget[]>(`${this.apiUrl}/budgets`);
    }

    saveBudget(budget: Budget): Observable<Budget> {
        return this.http.post<Budget>(`${this.apiUrl}/budgets`, budget);
    }

    getAlerts(): Observable<Alert[]> {
        return this.http.get<Alert[]>(`${this.apiUrl}/budgets/alerts`);
    }

    // Dashboard
    getDashboardStats(startDate?: string, endDate?: string): Observable<DashboardStats> {
        let params: any = {};
        if (startDate) params.startDate = startDate;
        if (endDate) params.endDate = endDate;
        return this.http.get<DashboardStats>(`${this.apiUrl}/dashboard/stats`, { params });
    }
}
