import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Product } from '../product.model';
import { ProductService } from '../product.service';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {

  category: string = '';
  products: Product[] = [];

  constructor(
    private productService: ProductService, 
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(({ category }) => {
      this.category = category;

      if (category) {
        this.productService.listByCategory(this.category).subscribe(productList => {
          this.products = productList.products;
        });
      }
      else {
        this.productService.list().subscribe(productList => {
          this.products = productList.products;
        })
      }
    })
  }

}
