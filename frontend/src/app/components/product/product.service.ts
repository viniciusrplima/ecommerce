import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from './product.model';
import { Observable } from 'rxjs';
import { ProductList } from './product-list.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  baseUrl = "http://localhost:3000/products/"

  constructor(
    private http: HttpClient
  ) { }

  list(): Observable<ProductList> {
    return this.http.get<ProductList>(this.baseUrl);
  }

  listByCategory(category: string): Observable<ProductList> {
    return this.http.get<ProductList>(this.baseUrl + "?type=" + category);
  }

  get(id: number): Observable<Product> {
    return this.http.get<Product>(this.baseUrl + id);
  }

}
