//default
import { AfterViewInit, Component, Inject, OnInit } from '@angular/core';

//added
import { Article } from '../_models/article';
import { ArticleService } from '../_services/article.service';
import { ActivatedRoute } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormGroup, Validators, FormControl } from '@angular/forms';
import {NgIf} from '@angular/common';
import { AngularEditorModule } from '@kolkov/angular-editor';
import { DOCUMENT } from '@angular/common';
import { User } from '@app/_models/user';
import { AccountService } from '@app/_services/account.service';
import { AlertService } from '@app/_services/alert.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-article',
  imports: [FormsModule, ReactiveFormsModule, NgIf, AngularEditorModule],
  templateUrl: './article.component.html',
  styleUrl: './article.component.css'
})

export class ArticleComponent implements OnInit, AfterViewInit {
  
  article: Article = new Article();
  form!: FormGroup;
  submitted = false;
  editMode = false;
  articleLoaded = false;
  loadFailed = false;
  user?: User | null;

  constructor(
    private articleService: ArticleService,
    private route: ActivatedRoute,
    private accountService: AccountService,
    private alertService: AlertService,
    @Inject(DOCUMENT) document: Document
  ) {}
  
  ngOnInit(): void {
    this.accountService.user.subscribe(u => this.user = u);
    const articleId = parseInt(this.route.snapshot.paramMap.get('articleId')!);
    if (articleId !== 0) {
      this.getArticle(articleId);
    }
  }

  async ngAfterViewInit(): Promise<void> {
    while (!this.articleLoaded && !this.loadFailed) {
      console.log("It's not loaded.");
      await this.delay(20);
    }
    await this.delay(20);
    this.setUpAnchorLinks();
  }

  async delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  get title() {    
    return this.form.get('title');
  }

  get subtitle() {
    return this.form.get('subtitle');
  }

  get content() {
    return this.form.get('content');
  }

  getArticle(articleId: number) {
    this.articleService.getArticle(articleId).subscribe( {
      next: (article) => {
        this.article = article;
      
        this.form = new FormGroup({
          title: new FormControl(this.article.title, [Validators.required, Validators.maxLength(25)]),
          subtitle: new FormControl(this.article.subtitle, [Validators.required, Validators.maxLength(35)]),
          content: new FormControl(this.article.content, [Validators.required])
        });
        this.articleLoaded = true;
      },
      error: error => {
        this.loadFailed = true;
        this.alertService.error(this.inferErrorMessage(error));
      }      
    });
  }

  inferErrorMessage(errResponse: HttpErrorResponse): string {
      let message;
      
      if (errResponse.status == 404) {
        message = "Article not found!";
      } else {
        message = `An error occurred retrieving the article.  (Status Code: ${errResponse.status})`;
      }
  
      return message;
    }

  onSubmit() {
    this.submitted = true;

    // stop here if form is invalid
    if (this.form.invalid) {
        return;
    }

    this.updateArticle();
  }

  updateArticle() {
    this.editMode = false;
    this.article.title = this.title?.value;
    this.article.subtitle = this.subtitle?.value;
    this.article.content = this.content?.value;
    this.articleService.updateArticle(this.article.articleId, this.article).subscribe(() => {
      console.log("Update request processed");
      this.setUpAnchorLinks();
    });
  }

  async cancel(): Promise<void> {
    this.editMode = false;
    await this.delay(20);
    this.setUpAnchorLinks();
  }
  
  /**
   * Dynamically sets up anchor links by looking for an empty <div> with a class name of anchor-links.
   * If found, it further looks for any <h2>'s with a class name of anchor-point and builds
   * link-styled buttons with a click function which calls scrollTo to scroll to the appropriate
   * <h2> location.
   * 
   * @remarks
   * This was done because 1) Angular does not handle anchor links very well and 2) does not document
   * a way to use them with content originating from an HttpClient call.
   */
  setUpAnchorLinks() {
    // get the anchorLinksUl element to later attach things to
    let anchorLinksDiv;
    let elements = document.querySelectorAll('div.anchor-links');
    
    for (let element of elements) {
      anchorLinksDiv = element;
      break;
    }

    if (anchorLinksDiv != null) {
      let anchorPoints = document.querySelectorAll('h2.anchor-point');

      // iterate through the anchorPoints and add them to anchor-links div
      for (let element of anchorPoints) {
        let button = document.createElement("button");
        let anchorPoint = element.getBoundingClientRect().top + 20;
        let func = function() { scrollTo({top: anchorPoint, left: 0, behavior: 'smooth'}); };
        button.innerText = element.textContent!;
        button.className = "btn btn-link anchor-link";
        button.addEventListener("click", func, false);
  
        anchorLinksDiv.appendChild(button);
        anchorLinksDiv.appendChild(document.createElement("br"));
      }
    }
  }
}
