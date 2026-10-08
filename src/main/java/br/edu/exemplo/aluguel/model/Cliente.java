package br.edu.exemplo.aluguel.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    private String telefone;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Aluguel> alugueis = new ArrayList<>();

    public Cliente() { }

    public Cliente(String nome, String cpf, String telefone) {
        this.nome = nome;
        setCpf(cpf);
        setTelefone(telefone);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }
    public List<Aluguel> getAlugueis() { return alugueis; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) {
        validarApenasNumeros(cpf, "CPF");
        this.cpf = cpf;
    }
    public void setTelefone(String telefone) {
        validarApenasNumeros(telefone, "Telefone");
        this.telefone = telefone;
    }

    private void validarApenasNumeros(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ficar vazio.");
        }
        if (!valor.matches("\\d+")) {
            throw new IllegalArgumentException(campo + " deve conter apenas números.");
        }
    }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nome='" + nome + "', cpf='" + cpf + "'}";
    }
}


