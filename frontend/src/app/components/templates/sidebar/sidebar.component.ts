import { Component, OnInit } from '@angular/core';
import { ProductType } from '../../product-type/product-type.model';
import { ProductTypeService } from '../../product-type/product-type.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  productTypes: ProductType[] = [];

  constructor(
    private productTypeService: ProductTypeService
  ) { }

  ngOnInit(): void {
    this.productTypeService.list().subscribe(productTypes => {
      this.productTypes = productTypes;
    })
  }

}
