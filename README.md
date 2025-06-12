# LapakTa 🛍️

**LapakTa** adalah aplikasi mobile e-commerce berbasis Android yang dirancang sebagai Proyek Final untuk mata kuliah Laboratorium Pemrograman Mobile 2025. Aplikasi ini memungkinkan pengguna untuk menjelajahi berbagai produk, melihat detailnya, menambahkannya ke keranjang, dan melakukan proses checkout, lengkap dengan sistem registrasi, login, dan kustomisasi profil pengguna.

![LapakTa Logo](https://i.imgur.com/f1tMNP7.png) 

---

## 🚀 Fitur Utama

Aplikasi LapakTa dilengkapi dengan berbagai fitur untuk memberikan pengalaman belanja online yang lengkap:

- **Jelajah & Pencarian Produk**: Menampilkan daftar produk dari API pada halaman utama dan memungkinkan pengguna untuk melakukan pencarian produk.
- **Detail Produk Lengkap**: Halaman khusus yang menampilkan semua detail produk, termasuk galeri gambar, harga diskon, stok, informasi pengiriman, garansi, dan ulasan pengguna.
- **Keranjang Belanja Fungsional**:
    - Menambah produk ke keranjang dengan dialog pemilih kuantitas.
    - Mengelola kuantitas (tambah/kurang) langsung di dalam keranjang.
    - Menampilkan subtotal harga per item.
    - Menghapus item dari keranjang dengan dialog konfirmasi.
- **Alur Checkout**: Halaman khusus untuk checkout di mana pengguna dapat mengisi informasi pengiriman dan memilih metode pembayaran.
- **Manajemen Akun Pengguna**:
    - Sistem registrasi dan login pengguna yang persisten dengan data lengkap.
    - Halaman "Akun Saya" yang menampilkan data pengguna, lengkap dengan fitur ubah foto profil.
    - Fitur "Ubah Profil" dan "Ubah Password".
    - Fitur Logout.
- **Dukungan Offline**:
    - Data produk disimpan di *cache* sehingga tetap dapat dilihat saat tidak ada koneksi internet.
    - Keranjang belanja dan sesi login bersifat persisten.
- **Tema Ganda**: Mendukung mode Terang (Light) dan Gelap (Dark) yang dapat diubah melalui halaman Akun.

---

## 📸 Screenshot Aplikasi

| Halaman Login | Halaman Registrasi | Halaman Utama (Home) |
| :---: |:---:|:---:|
| ![Halaman Login](https://i.imgur.com/jOxvbeq.png) | ![Halaman Registrasi](https://i.imgur.com/yh32p55.png) | ![Halaman Utama](https://i.imgur.com/Cfv5pa6.png) |

| Detail Produk | Keranjang (Terang) | Keranjang (Gelap) |
| :---: |:---:|:---:|
| ![Halaman Detail Produk](https://i.imgur.com/5gb8GHe.png) | ![Keranjang Belanja](https://i.imgur.com/my1zyJ9.png) | ![Keranjang Belanja Mode Gelap](https://i.imgur.com/93KaNvX.png) |

---

## 🛠️ Spesifikasi & Implementasi Teknis

Proyek ini dibangun untuk memenuhi semua spesifikasi teknis yang diwajibkan:

- **Activity & Intent**: Menggunakan beberapa `Activity` (`Splash`, `Login`, `Register`, `Main`, `Detail`, `Checkout`, `EditProfile`, `ChangePassword`) dan `Intent` untuk navigasi serta pengiriman data `Parcelable`.
- **RecyclerView**: Digunakan untuk menampilkan daftar produk, item keranjang, dan ulasan pengguna.
- **Fragment & Navigation Component**: Navigasi antar-fragment (Home, Cart, Account) dikelola oleh Jetpack Navigation Component.
- **Background Thread**: Operasi jaringan untuk mengambil data dari API dijalankan di *background thread* menggunakan `Executor` dan `Handler`.
- **Networking**: Mengambil data produk dari API publik (dummyjson.com) menggunakan library `Retrofit`.
- **Local Data Persistence**: `SharedPreferences` digunakan secara ekstensif untuk menyimpan *cache* data produk, data keranjang belanja, preferensi tema, dan semua informasi sesi pengguna, termasuk path foto profil.
- **File Storage**: Menyimpan file gambar profil yang dipilih pengguna ke dalam penyimpanan internal aplikasi untuk persistensi data.
- **UI/UX**:
    - Desain antarmuka yang modern, elegan, dan minimalis mengikuti prinsip Material Design dengan tema warna yang konsisten.
    - Mendukung tema Terang dan Gelap (`DayNight`) yang adaptif untuk semua ikon dan komponen.
    - Menggunakan *Shimmer Effect* untuk *placeholder loading* yang lebih baik.
    - Dialog konfirmasi untuk aksi-aksi penting (hapus item, logout).

---

## ⚙️ Cara Penggunaan

1.  **Clone Repositori**:
    ```bash
    git clone [https://github.com/xebec51/LapakTa.git](https://github.com/xebec51/LapakTa.git)
    ```
2.  **Buka di Android Studio**: Buka folder proyek melalui Android Studio.
3.  **Jalankan Aplikasi**: Lakukan `Build` dan `Run` pada emulator atau perangkat Android fisik.