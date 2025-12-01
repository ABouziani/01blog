import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FooterComponent } from './footer';


describe('Footer', () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should render footer text', () => {
  const compiled = fixture.nativeElement as HTMLElement;
  expect(compiled.textContent).toContain('2025 01Blog');
});
});
