import { Component, OnInit } from '@angular/core';
import { Product } from '../product.model';
import { ProductService } from '../product.service';

@Component({
  selector: 'app-product-read',
  templateUrl: './product-read.component.html',
  styleUrls: ['./product-read.component.css']
})
export class ProductReadComponent implements OnInit {

  product?: Product;

  constructor(
    private productService: ProductService
  ) { }

  ngOnInit(): void {
    this.productService.get(8).subscribe(product => {
      this.product = product;
    })
  }

}
