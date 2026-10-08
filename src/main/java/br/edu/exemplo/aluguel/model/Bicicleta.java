package br.edu.exemplo.aluguel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bicicletas")
public class Bicicleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    private String tipo;

    @Column(nullable = false)
    private BigDecimal valorDiaria;

    @Column(nullable = false)
    private boolean disponivel = true;

    @OneToMany(mappedBy = "bicicleta", fetch = FetchType.LAZY)
    private List<Aluguel> alugueis = new ArrayList<>();

    public Bicicleta() { }

    public Bicicleta(String marca, String modelo, String tipo, BigDecimal valorDiaria) {
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
        this.valorDiaria = valorDiaria;
    }

    public Long getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getTipo() { return tipo; }
    public BigDecimal getValorDiaria() { return valorDiaria; }
    public boolean isDisponivel() { return disponivel; }
    public List<Aluguel> getAlugueis() { return alugueis; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setValorDiaria(BigDecimal valorDiaria) { this.valorDiaria = valorDiaria; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    @Override
    public String toString() {
        return "Bicicleta{id=" + id + ", " + marca + " " + modelo + ", disponivel=" + disponivel + "}";
    }
}
