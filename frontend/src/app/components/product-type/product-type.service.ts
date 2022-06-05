import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProductType } from './product-type.model';

@Injectable({
  providedIn: 'root'
})
export class ProductTypeService {

  baseUrl = "http://localhost:3000/product-types/";

  constructor(
    private http: HttpClient
  ) { }

  list(): Observable<ProductType[]> {
    return this.http.get<ProductType[]>(this.baseUrl);
  }
}
