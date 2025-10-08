# Etapa 1: Use uma imagem base do Node.js. A versão 'alpine' é leve.
FROM node:18-alpine AS development

# Etapa 2: Defina o diretório de trabalho dentro do container
WORKDIR /app

# Etapa 3: Copie o package.json e o package-lock.json para o container
# Isso otimiza o cache do Docker. A instalação das dependências só será refeita se estes arquivos mudarem.
COPY package*.json ./

# Etapa 4: Instale as dependências do projeto
RUN npm install --legacy-peer-deps

# Etapa 5: Copie o restante do código da aplicação para o container
COPY . .

# Etapa 6: Exponha a porta padrão do servidor de desenvolvimento do Angular
EXPOSE 4200

# Etapa 7: Comando para iniciar a aplicação em modo de desenvolvimento
# O '--host 0.0.0.0' é ESSENCIAL para que a aplicação seja acessível de fora do container.
CMD ["npm", "start", "--", "--host", "0.0.0.0"]