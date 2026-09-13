import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) {

        int porta = 5000;

        try {
            // Cria o servidor TCP na porta 5000.
            ServerSocket servidor = new ServerSocket(porta);

            System.out.println("Servidor iniciado na porta " + porta);
            System.out.println("Aguardando conexão de um cliente...");

            // Aguarda até que um cliente se conecte.
            Socket cliente = servidor.accept();

            System.out.println("Cliente conectado com sucesso!");

            // Entrada de dados: recebe comandos, textos, números e arquivos do cliente.
            DataInputStream entrada = new DataInputStream(cliente.getInputStream());

            // Saída de dados: envia respostas, textos, números e arquivos para o cliente.
            DataOutputStream saida = new DataOutputStream(cliente.getOutputStream());

            boolean continuar = true;

            while (continuar) {

                // Recebe o comando enviado pelo cliente.
                String comando = entrada.readUTF();

                System.out.println("Comando recebido: " + comando);

                if (comando.equalsIgnoreCase("LISTAR")) {
                    listarArquivos(saida);

                } else if (comando.equalsIgnoreCase("ENVIAR")) {
                    receberArquivo(entrada, saida);

                } else if (comando.equalsIgnoreCase("BAIXAR")) {
                    enviarArquivoParaCliente(entrada, saida);

                } else if (comando.equalsIgnoreCase("SAIR")) {
                    System.out.println("Cliente encerrou a conexão.");
                    continuar = false;

                } else {
                    saida.writeUTF("Comando desconhecido.");
                }
            }

            entrada.close();
            saida.close();
            cliente.close();
            servidor.close();

            System.out.println("Servidor encerrado.");

        } catch (IOException erro) {
            System.out.println("Erro no servidor: " + erro.getMessage());
        }
    }

    // Este método lista os arquivos existentes na pasta arquivos_servidor.
    // Ele envia a quantidade de arquivos e depois envia o nome de cada arquivo.
    public static void listarArquivos(DataOutputStream saida) throws IOException {

        File pasta = obterPastaServidor();;

        File[] arquivos = pasta.listFiles();

        if (arquivos == null || arquivos.length == 0) {
            saida.writeInt(0);
            return;
        }

        int quantidadeArquivos = 0;

        for (File arquivo : arquivos) {
            if (arquivo.isFile()) {
                quantidadeArquivos++;
            }
        }

        saida.writeInt(quantidadeArquivos);

        for (File arquivo : arquivos) {
            if (arquivo.isFile()) {
                saida.writeUTF(arquivo.getName());
            }
        }
    }

    // Este método recebe um arquivo enviado pelo cliente.
    // Primeiro recebe o nome do arquivo, depois o tamanho e por fim o conteúdo em bytes.
    public static void receberArquivo(DataInputStream entrada, DataOutputStream saida) throws IOException {

        String nomeArquivo = entrada.readUTF();
        long tamanhoArquivo = entrada.readLong();

        File pasta = obterPastaServidor();
        File arquivoRecebido = new File(pasta, nomeArquivo);
        FileOutputStream arquivoSaida = new FileOutputStream(arquivoRecebido);

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

        System.out.println("Arquivo recebido: " + nomeArquivo);
        saida.writeUTF("Arquivo enviado com sucesso para o servidor.");
    }

    // Este método verifica se a pasta arquivos_servidor existe
    // dentro do diretório atual da aplicação.
    // Caso não exista, cria a pasta automaticamente.
    public static File obterPastaServidor() {

        String diretorioAtual = System.getProperty("user.dir");

        File pasta = new File(diretorioAtual, "arquivos_servidor");

        if (!pasta.exists()) {
            pasta.mkdirs();
            System.out.println("Pasta arquivos_servidor criada em: " + pasta.getAbsolutePath());
        }

        return pasta;
    }

    // Este método envia um arquivo do servidor para o cliente.
    // O servidor procura o arquivo na pasta arquivos_servidor.
    // Se encontrar, envia uma confirmação, o tamanho e o conteúdo do arquivo.
    public static void enviarArquivoParaCliente(DataInputStream entrada, DataOutputStream saida) throws IOException {

        String nomeArquivo = entrada.readUTF();

        File pasta = obterPastaServidor();
        File arquivo = new File(pasta, nomeArquivo);

        if (!arquivo.exists() || !arquivo.isFile()) {
            saida.writeBoolean(false);
            saida.writeUTF("Arquivo não encontrado no servidor.");
            return;
        }

        saida.writeBoolean(true);
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

        System.out.println("Arquivo enviado para o cliente: " + nomeArquivo);
    }
}