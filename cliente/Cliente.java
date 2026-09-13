import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {

        String enderecoServidor = "localhost";
        int porta = 5000;

        try {
            // Cria uma conexão com o servidor.
            Socket socket = new Socket(enderecoServidor, porta);

            // Entrada de dados: recebe respostas, nomes, números e arquivos do servidor.
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            // Saída de dados: envia comandos, nomes, números e arquivos para o servidor.
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());

            Scanner teclado = new Scanner(System.in);

            System.out.println("Cliente conectado ao servidor com sucesso!");

            int opcao;

            do {
                System.out.println("\n===== SiCA - Cliente =====");
                System.out.println("1 - Listar arquivos do servidor");
                System.out.println("2 - Enviar arquivo para o servidor");
                System.out.println("3 - Baixar arquivo do servidor");
                System.out.println("4 - Sair");
                System.out.print("Escolha uma opção: ");

                opcao = teclado.nextInt();
                teclado.nextLine();

                if (opcao == 1) {
                    saida.writeUTF("LISTAR");
                    listarArquivosServidor(entrada);

                } else if (opcao == 2) {
    enviarArquivo(teclado, saida, entrada);

                } else if (opcao == 3) {
                    saida.writeUTF("BAIXAR");
                    baixarArquivo(teclado, saida, entrada);

                } else if (opcao == 4) {
                    saida.writeUTF("SAIR");
                    System.out.println("Encerrando cliente.");

                } else {
                    System.out.println("Opção inválida.");
                }

            } while (opcao != 4);

            teclado.close();
            entrada.close();
            saida.close();
            socket.close();

        } catch (IOException erro) {
            System.out.println("Erro no cliente: " + erro.getMessage());
        }
    }

    // Este método recebe do servidor a quantidade de arquivos disponíveis.
    // Depois recebe e exibe o nome de cada arquivo.
    public static void listarArquivosServidor(DataInputStream entrada) throws IOException {

        int quantidadeArquivos = entrada.readInt();

        if (quantidadeArquivos == 0) {
            System.out.println("Nenhum arquivo disponível no servidor.");
            return;
        }

        System.out.println("Arquivos disponíveis no servidor:");

        for (int i = 0; i < quantidadeArquivos; i++) {
            String nomeArquivo = entrada.readUTF();
            System.out.println("- " + nomeArquivo);
        }
    }

    // Este método envia um arquivo do cliente para o servidor.
    // Ele pede o caminho completo do arquivo, verifica se existe,
    // envia o nome, o tamanho e o conteúdo do arquivo.
    public static void enviarArquivo(
            Scanner teclado,
            DataOutputStream saida,
            DataInputStream entrada
    ) throws IOException {

        System.out.print("Digite o caminho completo do arquivo: ");
        String caminhoArquivo = teclado.nextLine();

        File arquivo = new File(caminhoArquivo);

       if (!arquivo.exists() || !arquivo.isFile()) {
    System.out.println("Arquivo não encontrado.");
    return;
}

// Só envia o comando ENVIAR depois de confirmar que o arquivo existe.
saida.writeUTF("ENVIAR");

saida.writeUTF(arquivo.getName());
saida.writeLong(arquivo.length());

        FileInputStream arquivoEntrada = new FileInputStream(arquivo);

        byte[] buffer = new byte[4096];

        int quantidadeLida;

        while ((quantidadeLida = arquivoEntrada.read(buffer)) != -1) {
            saida.write(buffer, 0, quantidadeLida);
        }

        saida.flush();
        arquivoEntrada.close();

        String resposta = entrada.readUTF();
        System.out.println(resposta);
    }

    // Este método baixa um arquivo do servidor.
    // O usuário digita o nome do arquivo.
    // O cliente solicita esse arquivo ao servidor e salva na pasta downloads_cliente.
    public static void baixarArquivo(
            Scanner teclado,
            DataOutputStream saida,
            DataInputStream entrada
    ) throws IOException {

        System.out.print("Digite o nome do arquivo que deseja baixar: ");
        String nomeArquivo = teclado.nextLine();

        saida.writeUTF(nomeArquivo);

        boolean arquivoExiste = entrada.readBoolean();

        if (!arquivoExiste) {
            String mensagemErro = entrada.readUTF();
            System.out.println(mensagemErro);
            return;
        }

        String nomeArquivoRecebido = entrada.readUTF();
        long tamanhoArquivo = entrada.readLong();
   

        File pasta = obterPastaCliente();
        File arquivoBaixado = new File(pasta, nomeArquivoRecebido);

        FileOutputStream arquivoSaida = new FileOutputStream(arquivoBaixado);

        byte[] buffer = new byte[4096];

        long bytesRecebidos = 0;

        while (bytesRecebidos < tamanhoArquivo) {
            int quantidadeLida = entrada.read(buffer, 0,
                    (int) Math.min(buffer.length, tamanhoArquivo - bytesRecebidos));

            if (quantidadeLida == -1) {
                break;
            }

            arquivoSaida.write(buffer, 0, quantidadeLida);
            bytesRecebidos += quantidadeLida;
        }

        arquivoSaida.close();

        System.out.println("Arquivo baixado com sucesso em: downloads_cliente/" + nomeArquivoRecebido);
    }

    // Este método verifica se a pasta downloads_cliente existe
    // dentro do diretório atual da aplicação.
    // Caso não exista, cria a pasta automaticamente.
    public static File obterPastaCliente() {

        String diretorioAtual = System.getProperty("user.dir");

        File pasta = new File(diretorioAtual, "downloads_cliente");

        if (!pasta.exists()) {
            pasta.mkdirs();
            System.out.println("Pasta downloads_cliente criada em: " + pasta.getAbsolutePath());
        }

        return pasta;
    }
}