package br.edu.exemplo.aluguel;

import br.edu.exemplo.aluguel.config.HibernateUtil;
import br.edu.exemplo.aluguel.dao.AluguelDao;
import br.edu.exemplo.aluguel.dao.BicicletaDao;
import br.edu.exemplo.aluguel.dao.ClienteDao;
import br.edu.exemplo.aluguel.model.Aluguel;
import br.edu.exemplo.aluguel.model.Bicicleta;
import br.edu.exemplo.aluguel.model.Cliente;
import br.edu.exemplo.aluguel.service.AluguelService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import org.hibernate.HibernateException;

/** Ponto de entrada do sistema: menu de terminal e chamadas aos DAOs/serviço. */
public class Main {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ClienteDao clienteDao = new ClienteDao();
    private static final BicicletaDao bicicletaDao = new BicicletaDao();
    private static final AluguelDao aluguelDao = new AluguelDao();
    private static final AluguelService aluguelService = new AluguelService();
    private static final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            int opcao;
            do {
                exibirMenu();
                opcao = lerInteiro("Escolha uma opção: ");
                try { executarOpcao(opcao); }
                catch (Exception e) { System.out.println("Operação não realizada: " + mensagemAmigavel(e)); }
            } while (opcao != 0);
        } finally {
            entrada.close();
            HibernateUtil.shutdown();
        }
    }

    private static void exibirMenu() {
        System.out.println("\n===== SISTEMA DE ALUGUEL DE BICICLETAS =====");
        System.out.println("1 - Cadastrar cliente\n2 - Listar clientes\n3 - Atualizar cliente\n4 - Excluir cliente");
        System.out.println("5 - Cadastrar bicicleta\n6 - Listar bicicletas\n7 - Atualizar bicicleta\n8 - Excluir bicicleta");
        System.out.println("9 - Realizar aluguel\n10 - Listar aluguéis\n11 - Finalizar aluguel\n0 - Sair");
    }

    private static void executarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> cadastrarCliente(); case 2 -> listarClientes(); case 3 -> atualizarCliente(); case 4 -> excluirCliente();
            case 5 -> cadastrarBicicleta(); case 6 -> listarBicicletas(); case 7 -> atualizarBicicleta(); case 8 -> excluirBicicleta();
            case 9 -> realizarAluguel(); case 10 -> listarAlugueis(); case 11 -> finalizarAluguel();
            case 0 -> System.out.println("Sistema encerrado."); default -> System.out.println("Opção inválida.");
        }
    }

    private static void cadastrarCliente() {
        Cliente cliente = new Cliente(lerTexto("Nome: "), lerNumeros("CPF: "), lerNumeros("Telefone: "));
        clienteDao.salvar(cliente);
        System.out.println("Cliente cadastrado com ID " + cliente.getId() + ".");
    }

    private static void listarClientes() {
        var clientes = clienteDao.listarTodos();
        if (clientes.isEmpty()) System.out.println("Nenhum cliente cadastrado.");
        else clientes.forEach(c -> System.out.printf("ID: %d | Nome: %s | CPF: %s | Telefone: %s%n", c.getId(), c.getNome(), c.getCpf(), c.getTelefone()));
    }

    private static void atualizarCliente() {
        Cliente cliente = buscarClientePeloId();
        cliente.setNome(lerTexto("Novo nome: ")); cliente.setCpf(lerNumeros("Novo CPF: ")); cliente.setTelefone(lerNumeros("Novo telefone: "));
        clienteDao.atualizar(cliente);
        System.out.println("Cliente atualizado.");
    }

    private static void excluirCliente() {
        Cliente cliente = buscarClientePeloId();
        if (aluguelDao.existeParaCliente(cliente.getId())) { System.out.println("Não é possível excluir: o cliente possui aluguéis registrados."); return; }
        clienteDao.excluir(cliente.getId());
        System.out.println("Cliente excluído.");
    }

    private static void cadastrarBicicleta() {
        Bicicleta bicicleta = new Bicicleta(lerTexto("Marca: "), lerTexto("Modelo: "), lerTexto("Tipo: "), lerValorPositivo("Valor da diária: R$ "));
        bicicletaDao.salvar(bicicleta);
        System.out.println("Bicicleta cadastrada com ID " + bicicleta.getId() + ".");
    }

    private static void listarBicicletas() {
        var bicicletas = bicicletaDao.listarTodos();
        if (bicicletas.isEmpty()) System.out.println("Nenhuma bicicleta cadastrada.");
        else bicicletas.forEach(b -> System.out.printf("ID: %d | %s %s | Tipo: %s | Diária: R$ %s | %s%n", b.getId(), b.getMarca(), b.getModelo(), b.getTipo(), b.getValorDiaria(), b.isDisponivel() ? "Disponível" : "Alugada"));
    }

    private static void atualizarBicicleta() {
        Bicicleta bicicleta = buscarBicicletaPeloId();
        bicicleta.setMarca(lerTexto("Nova marca: ")); bicicleta.setModelo(lerTexto("Novo modelo: ")); bicicleta.setTipo(lerTexto("Novo tipo: "));
        bicicleta.setValorDiaria(lerValorPositivo("Novo valor da diária: R$ "));
        bicicletaDao.atualizar(bicicleta);
        System.out.println("Bicicleta atualizada.");
    }

    private static void excluirBicicleta() {
        Bicicleta bicicleta = buscarBicicletaPeloId();
        if (aluguelDao.existeParaBicicleta(bicicleta.getId())) { System.out.println("Não é possível excluir: a bicicleta possui aluguéis registrados."); return; }
        bicicletaDao.excluir(bicicleta.getId());
        System.out.println("Bicicleta excluída.");
    }

    private static void realizarAluguel() {
        long clienteId = lerLong("ID do cliente: "); long bicicletaId = lerLong("ID da bicicleta: ");
        LocalDate inicio = lerData("Data de início (dd/MM/aaaa): "); LocalDate fim = lerData("Data prevista de devolução (dd/MM/aaaa): ");
        Aluguel aluguel = aluguelService.realizar(clienteId, bicicletaId, inicio, fim);
        System.out.println("Aluguel realizado com ID " + aluguel.getId() + ". Valor total: R$ " + aluguel.getValorTotal());
    }

    private static void listarAlugueis() {
        var alugueis = aluguelDao.listarTodosComRelacionamentos();
        if (alugueis.isEmpty()) System.out.println("Nenhum aluguel cadastrado.");
        else alugueis.forEach(a -> System.out.printf("ID: %d | Cliente: %s | Bicicleta: %s %s | Início: %s | Prevista: %s | Devolução: %s | Total: R$ %s%n", a.getId(), a.getCliente().getNome(), a.getBicicleta().getMarca(), a.getBicicleta().getModelo(), a.getDataInicio().format(FORMATO_DATA), a.getDataFimPrevista().format(FORMATO_DATA), a.getDataDevolucao() == null ? "Pendente" : a.getDataDevolucao().format(FORMATO_DATA), a.getValorTotal()));
    }

    private static void finalizarAluguel() {
        long aluguelId = lerLong("ID do aluguel: "); LocalDate devolucao = lerData("Data de devolução (dd/MM/aaaa): ");
        Aluguel aluguel = aluguelService.finalizar(aluguelId, devolucao);
        System.out.println("Aluguel finalizado; a bicicleta está disponível novamente. Valor total: R$ " + aluguel.getValorTotal());
    }

    private static Cliente buscarClientePeloId() {
        Cliente cliente = clienteDao.buscarPorId(lerLong("ID do cliente: "));
        if (cliente == null) throw new IllegalArgumentException("Cliente não encontrado.");
        return cliente;
    }
    private static Bicicleta buscarBicicletaPeloId() {
        Bicicleta bicicleta = bicicletaDao.buscarPorId(lerLong("ID da bicicleta: "));
        if (bicicleta == null) throw new IllegalArgumentException("Bicicleta não encontrada.");
        return bicicleta;
    }
    private static String lerTexto(String mensagem) {
        System.out.print(mensagem); String texto = entrada.nextLine().trim();
        if (texto.isEmpty()) throw new IllegalArgumentException("O campo não pode ficar vazio.");
        return texto;
    }
    private static String lerNumeros(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            if (texto.matches("\\d+")) return texto;
            System.out.println("Digite apenas números.");
        }
    }
    private static long lerLong(String mensagem) {
        while (true) { try { return Long.parseLong(lerTexto(mensagem)); } catch (NumberFormatException e) { System.out.println("Digite um número inteiro válido."); } }
    }
    private static int lerInteiro(String mensagem) {
        while (true) { try { return Integer.parseInt(lerTexto(mensagem)); } catch (NumberFormatException e) { System.out.println("Digite um número inteiro válido."); } }
    }
    private static BigDecimal lerValorPositivo(String mensagem) {
        while (true) {
            try { BigDecimal valor = new BigDecimal(lerTexto(mensagem).replace(',', '.')); if (valor.signum() > 0) return valor; System.out.println("O valor deve ser maior que zero."); }
            catch (NumberFormatException e) { System.out.println("Digite um valor válido, por exemplo: 25,50."); }
        }
    }
    private static LocalDate lerData(String mensagem) {
        while (true) { try { return LocalDate.parse(lerTexto(mensagem), FORMATO_DATA); } catch (DateTimeParseException e) { System.out.println("Digite a data no formato dd/MM/aaaa."); } }
    }
    private static String mensagemAmigavel(Exception e) {
        if (e instanceof HibernateException) return "erro ao acessar os dados. Verifique as informações e tente novamente.";
        return e.getMessage() == null ? "erro inesperado." : e.getMessage();
    }
}

