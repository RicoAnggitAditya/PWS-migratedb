/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pabd_jaya.pabd_jaya;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author acer
 */
@Entity
@Table(name = "transaksi")
@NamedQueries({
    @NamedQuery(name = "Transaksi.findAll", query = "SELECT t FROM Transaksi t"),
    @NamedQuery(name = "Transaksi.findByKodeTransaksi", query = "SELECT t FROM Transaksi t WHERE t.kodeTransaksi = :kodeTransaksi"),
    @NamedQuery(name = "Transaksi.findByNamaObat", query = "SELECT t FROM Transaksi t WHERE t.namaObat = :namaObat"),
    @NamedQuery(name = "Transaksi.findByQty", query = "SELECT t FROM Transaksi t WHERE t.qty = :qty"),
    @NamedQuery(name = "Transaksi.findByTotal", query = "SELECT t FROM Transaksi t WHERE t.total = :total"),
    @NamedQuery(name = "Transaksi.findByTanggal", query = "SELECT t FROM Transaksi t WHERE t.tanggal = :tanggal")})
public class Transaksi implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "kode_transaksi")
    private Integer kodeTransaksi;
    @Basic(optional = false)
    @Column(name = "nama_obat")
    private String namaObat;
    @Basic(optional = false)
    @Column(name = "QTY")
    private int qty;
    @Basic(optional = false)
    @Column(name = "total")
    private int total;
    @Basic(optional = false)
    @Column(name = "tanggal")
    @Temporal(TemporalType.DATE)
    private Date tanggal;
    @JoinColumn(name = "kode_transaksi", referencedColumnName = "kode_obat", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Obat obat;

    public Transaksi() {
    }

    public Transaksi(Integer kodeTransaksi) {
        this.kodeTransaksi = kodeTransaksi;
    }

    public Transaksi(Integer kodeTransaksi, String namaObat, int qty, int total, Date tanggal) {
        this.kodeTransaksi = kodeTransaksi;
        this.namaObat = namaObat;
        this.qty = qty;
        this.total = total;
        this.tanggal = tanggal;
    }

    public Integer getKodeTransaksi() {
        return kodeTransaksi;
    }

    public void setKodeTransaksi(Integer kodeTransaksi) {
        this.kodeTransaksi = kodeTransaksi;
    }

    public String getNamaObat() {
        return namaObat;
    }

    public void setNamaObat(String namaObat) {
        this.namaObat = namaObat;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Obat getObat() {
        return obat;
    }

    public void setObat(Obat obat) {
        this.obat = obat;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kodeTransaksi != null ? kodeTransaksi.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transaksi)) {
            return false;
        }
        Transaksi other = (Transaksi) object;
        if ((this.kodeTransaksi == null && other.kodeTransaksi != null) || (this.kodeTransaksi != null && !this.kodeTransaksi.equals(other.kodeTransaksi))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pabd_jaya.pabd_jaya.Transaksi[ kodeTransaksi=" + kodeTransaksi + " ]";
    }
    
}
