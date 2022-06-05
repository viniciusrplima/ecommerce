import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductListComponent } from './components/product/product-list/product-list.component';
import { ProductReadComponent } from './components/product/product-read/product-read.component';
import { ProductsComponent } from './views/products/products.component';

const routes: Routes = [{
  path: "products",
  component: ProductsComponent,
  children: [{
    path: "",
    component: ProductListComponent
  },
  {
    path: ":id",
    component: ProductReadComponent
  }]
}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
