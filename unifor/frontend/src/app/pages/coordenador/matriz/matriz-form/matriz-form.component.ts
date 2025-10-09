import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatrizCurricular } from './../../../../model/MatrizCurricular';
import { MatrizService } from 'src/app/services/matriz.service';
import { Semestre } from 'src/app/model/Semestre';
import { Curso } from 'src/app/model/Curso';
import { CursoService } from 'src/app/services/curso.service';
import { SemestreService } from 'src/app/services/semestre.service';

@Component({
  standalone: true,
  selector: 'app-matriz-form',
  templateUrl: './matriz-form.component.html',
  styleUrls: ['./matriz-form.component.css'],
  imports: [CommonModule, RouterModule, FormsModule],
})
export class MatrizFormComponent implements OnInit {
  matrizCurricular: MatrizCurricular;
  sucesso: boolean = false;
  mensagemSucesso: string = '';
  erros: String[];
  id: number;
  idCurso?: number;
  idSemestre?: number;
  cursosDisponiveis: Curso[] = [];
  semestresDisponiveis: Semestre[] = [];

  constructor(
    private service: MatrizService,
    private cursoService: CursoService,
    private semestreService: SemestreService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
    this.matrizCurricular = new MatrizCurricular();  }
  

  ngOnInit(): void {
    document.getElementById('layoutSidenav_content')?.classList.add('semestre-ajuste');
    this.cursoService.listar().subscribe({
      next: (res) => (this.cursosDisponiveis = res),
      error: (err) => console.error('Erro ao carregar cursos:', err)
    });
  
    this.semestreService.listar().subscribe({
      next: (res) => (this.semestresDisponiveis = res),
      error: (err) => console.error('Erro ao carregar semestres:', err)
    });

    this.activatedRoute.params.subscribe(params => {
      this.id = params['id'];
      if (this.id) {
        this.service.buscarPorId(this.id).subscribe({
          next: (response) => {            
            this.matrizCurricular = response;
          },
          error: (errorResponse) => {            
            this.matrizCurricular = new MatrizCurricular();
          }
        });
      }
    });
  }

  voltarParaListagem() {
    this.router.navigate(['/coordenador/matriz/lista'])
  }  

  onSubmit() {
   if (!this.idCurso|| !this.idSemestre) {
      this.sucesso = false;
      this.erros = ['Preencha todos os campos obrigatórios.'];
      return;
    }
  
    const dto = {
      cursoId: this.idCurso,
      semestreId: this.idSemestre,
      ativa: this.matrizCurricular.ativa
    };    
  
    const tratarErro = (errorResponse: any, acao: string) => {
      this.sucesso = false;
      if (errorResponse?.error?.violations?.length) {
        this.erros = errorResponse.error.violations.map((v: any) => {
          const nomeCampo = v.field?.split('.')?.pop() || 'Campo';
          return `${nomeCampo}: ${v.message}`;
        });
      } else if (errorResponse?.error?.message) {
        this.erros = [errorResponse.error.message];
      } else {
        this.erros = [`Erro ao ${acao} Matriz Curricular.`];
      }
    };
  
    if (this.matrizCurricular.id) {
      this.service.atualizarMatriz(this.matrizCurricular.id, dto).subscribe({
        next: () => {
          this.sucesso = true;
          this.mensagemSucesso = 'Cadastro atualizado com sucesso!';
          this.erros = null;
        },
        error: (errorResponse) => tratarErro(errorResponse, 'atualizar')
      });
    } else {      
      this.service.salvarMatriz(dto).subscribe({
        next: (response) => {
          this.sucesso = true;
          this.mensagemSucesso = 'Cadastro realizado com sucesso!';
          this.erros = null;
          this.matrizCurricular = response;
        },
        error: (errorResponse) => tratarErro(errorResponse, 'salvar')
      });
    }
  } 
   
  ngOnDestroy(): void {
    document.getElementById('layoutSidenav_content')?.classList.remove('semestre-ajuste');
  }
}
