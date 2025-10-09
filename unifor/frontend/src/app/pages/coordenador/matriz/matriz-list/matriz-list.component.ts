import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatrizCurricular } from './../../../../model/MatrizCurricular';
import { MatrizService } from 'src/app/services/matriz.service';
import { FormsModule } from '@angular/forms';
import { CursoService } from 'src/app/services/curso.service';
import { SemestreService } from 'src/app/services/semestre.service';
import { Curso } from 'src/app/model/Curso';
import { Semestre } from 'src/app/model/Semestre';

@Component({
  standalone: true,
  selector: 'app-matriz-list',
  templateUrl: './matriz-list.component.html',
  styleUrls: ['./matriz-list.component.css'],
  imports: [CommonModule, RouterModule, FormsModule]
})
export class MatrizListComponent implements OnInit {
  matrizCurricularList: MatrizCurricular[] = [];
  matrizCurricularSelecionada: MatrizCurricular;
  mensagemSucesso: string;
  mensagemErro: string;
  usuarioLogado: any;   
  cursosDisponiveis: Curso[] = [];
  semestresDisponiveis: Semestre[] = [];
  carregandoDados: boolean = true;

  constructor(
    private semestreService: SemestreService,
    private cursoService: CursoService,
    private service: MatrizService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    document.getElementById('layoutSidenav_content')?.classList.add('semestre-ajuste');    
    const usuarioSalvo = sessionStorage.getItem('usuarioLogado');
    if (usuarioSalvo) {
      this.usuarioLogado = JSON.parse(usuarioSalvo);     
    }

    this.service.listar().subscribe({
      next: (resposta) => {
        this.matrizCurricularList = resposta;        
      },
      error: (err) => console.error('Erro ao buscar matriz curricular:', err)
    });
    
     this.cursoService.listar().subscribe({
      next: (res) => this.cursosDisponiveis = res,
      error: (err) => console.error('Erro ao carregar cursos:', err)
    });
   
    this.semestreService.listar().subscribe({
      next: (res) => this.semestresDisponiveis = res,
      error: (err) => console.error('Erro ao carregar semestres:', err)
    });
  }  

  novoCadastro() {
    this.router.navigate(['/coordenador/matriz/form'])
  }

  exibirMatrizModalDelet(usuario: MatrizCurricular) {
    this.matrizCurricularSelecionada = usuario;
  }

  deletarMatriz() {
    this.service
      .deletar(this.matrizCurricularSelecionada.id)
      .subscribe(
        response => {
          this.mensagemSucesso = 'Matriz curricular deletada com sucesso!'
          this.ngOnInit();
        },
        erro => this.mensagemErro = 'Ocorreu um erro ao deletar a Matriz curricular.')
  }

  temRole(role: string): boolean {
    if (!this.usuarioLogado || !this.usuarioLogado.roles) return false;
    return this.usuarioLogado.roles.some(r => r.includes(role));
  }

  ngOnDestroy(): void {
    document.getElementById('layoutSidenav_content')?.classList.remove('semestre-ajuste');
  }
}
