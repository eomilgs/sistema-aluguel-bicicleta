package br.edu.trabalho.aluguel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "alugueis")
public class Aluguel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFimPrevista;

    private LocalDate dataDevolucao;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bicicleta_id", nullable = false)
    private Bicicleta bicicleta;

    public Aluguel() { }

    public Aluguel(LocalDate dataInicio, LocalDate dataFimPrevista, BigDecimal valorTotal,
                   Cliente cliente, Bicicleta bicicleta) {
        this.dataInicio = dataInicio;
        this.dataFimPrevista = dataFimPrevista;
        this.valorTotal = valorTotal;
        this.cliente = cliente;
        this.bicicleta = bicicleta;
    }

    public Long getId() { return id; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFimPrevista() { return dataFimPrevista; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public Cliente getCliente() { return cliente; }
    public Bicicleta getBicicleta() { return bicicleta; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    @Override
    public String toString() {
        return "Aluguel{id=" + id + ", inicio=" + dataInicio + ", fim=" + dataFimPrevista
                + ", total=R$ " + valorTotal + "}";
    }
}