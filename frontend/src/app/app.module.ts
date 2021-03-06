import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/templates/header/header.component';
import { ProductReadComponent } from './components/product/product-read/product-read.component';

import { HttpClientModule } from '@angular/common/http';

import localePt from '@angular/common/locales/pt';
import { registerLocaleData } from '@angular/common';
import { SidebarComponent } from './components/templates/sidebar/sidebar.component';
import { ProductListComponent } from './components/product/product-list/product-list.component';
import { ProductsComponent } from './views/products/products.component';

registerLocaleData(localePt);

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    ProductReadComponent,
    SidebarComponent,
    ProductListComponent,
    ProductsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule, 
    HttpClientModule
  ],
  providers: [{
    provide: LOCALE_ID, 
    useValue: 'pt-BR'
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
