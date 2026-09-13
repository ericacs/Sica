# Sica

# SiCA - Sistema de Compartilhamento de Arquivos

A aplicação **SiCA** foi desenvolvida em **Java** utilizando **sockets TCP**, com o objetivo de permitir a comunicação entre um cliente e um servidor para realizar o compartilhamento de arquivos. Nesse sistema, o servidor deve ser iniciado primeiro, ficando responsável por aguardar conexões na porta `5000`. Depois que o cliente se conecta, ele pode escolher algumas opções em um menu, como listar os arquivos disponíveis no servidor, enviar um arquivo, baixar um arquivo armazenado ou encerrar a aplicação.

O funcionamento da aplicação ocorre por meio da troca de comandos entre cliente e servidor. O cliente envia comandos como `LISTAR`, `ENVIAR`, `BAIXAR` e `SAIR`, e o servidor interpreta cada um deles para executar a ação correspondente. Quando o comando enviado é `LISTAR`, o servidor acessa a pasta `arquivos_servidor`, verifica quais arquivos estão disponíveis e envia essa lista para o cliente. Caso essa pasta ainda não exista, o próprio servidor cria automaticamente a pasta no diretório atual da aplicação, utilizando o caminho obtido por meio de `System.getProperty("user.dir")`.

No envio de arquivos, o cliente informa o caminho do arquivo que deseja transferir. Depois disso, a aplicação envia para o servidor o nome do arquivo, o seu tamanho e o conteúdo em bytes. O servidor recebe essas informações e salva o arquivo dentro da pasta `arquivos_servidor`. Esse processo permite que arquivos do computador do cliente sejam armazenados no servidor de forma simples.

Já no download de arquivos, o cliente informa o nome do arquivo que deseja baixar. O servidor procura esse arquivo dentro da pasta `arquivos_servidor` e, caso ele exista, envia o conteúdo para o cliente. O cliente então salva o arquivo recebido na pasta `downloads_cliente`. Caso o arquivo não seja encontrado, o servidor retorna uma mensagem informando que o arquivo solicitado não está disponível.

O uso de **sockets TCP** foi escolhido porque esse tipo de comunicação é confiável e adequado para transferência de arquivos. Como os arquivos precisam chegar completos e na ordem correta, o TCP ajuda a evitar perdas ou problemas durante o envio dos dados entre cliente e servidor.

O código foi organizado em dois arquivos principais: `Servidor.java` e `Cliente.java`. O arquivo `Servidor.java` é responsável por iniciar o servidor, aguardar a conexão do cliente, receber comandos, listar arquivos, receber arquivos enviados e enviar arquivos solicitados. Já o arquivo `Cliente.java` é responsável por se conectar ao servidor, exibir o menu para o usuário e enviar os comandos correspondentes de acordo com a opção escolhida.

Ao executar o cliente, o usuário visualiza um menu com as opções de listar arquivos do servidor, enviar arquivo para o servidor, baixar arquivo do servidor e sair. Dessa forma, a aplicação atende aos principais requisitos propostos na atividade, permitindo o envio, a listagem e o download de arquivos por meio de uma comunicação cliente-servidor em Java. Além disso, os principais métodos foram comentados no código para facilitar a compreensão do funcionamento da aplicação.
