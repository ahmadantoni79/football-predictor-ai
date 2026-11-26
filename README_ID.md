# FOOTBALL PREDICTOR AI V1 — Android

Aplikasi Android native sederhana dengan antarmuka Bahasa Indonesia. Mengambil data dari API-Football (API-Sports).

## Fitur V1
- Simpan API key pada perangkat.
- Cari tim berdasarkan nama.
- Cari jadwal pertemuan mendatang kedua tim.
- Ambil endpoint prediksi untuk fixture yang ditemukan.
- Cari jadwal hari ini dan menyaring hingga Top 5 berdasarkan sinyal persentase dari endpoint prediksi.

## Kebutuhan data
1. Buat akun di https://www.api-football.com/ lalu ambil API key dari dashboard.
2. Paket gratis yang tercantum saat proyek dibuat: 100 request/hari. Batas dan cakupan bisa berubah; periksa halaman harga resmi.
3. Buka aplikasi, masukkan API key, lalu simpan.

## Membuat APK tanpa komputer (GitHub Actions)
1. Buat akun GitHub atau masuk ke akun yang sudah ada.
2. Buat repository baru bernama `football-predictor-ai`.
3. Unggah seluruh isi folder proyek ini ke repository (bisa melalui browser HP; menu Add file → Upload files).
4. Buka tab **Actions**, pilih workflow **Build Android APK**, lalu tekan **Run workflow**.
5. Setelah proses selesai, buka hasil run dan unduh artifact `Football-Predictor-AI-V1-APK`.
6. Ekstrak ZIP artifact, lalu instal `app-debug.apk` di Android. Jika diminta, izinkan instalasi dari sumber tersebut.

## Catatan teknis & keterbatasan V1
- API key disimpan pada SharedPreferences aplikasi dan dikirim dari perangkat ke API. Untuk distribusi publik, sebaiknya pindahkan key ke backend proxy dengan autentikasi; jangan membagikan APK berisi key pribadi.
- Top 5 V1 memeriksa maksimal 12 fixture pertama dari jadwal hari itu agar tidak menghabiskan kuota gratis. Persentase maksimum dari respons API dipakai hanya sebagai sinyal ranking, bukan probabilitas yang sudah dikalibrasi.
- Cakupan statistik, cedera, susunan pemain, odds, dan prediksi berbeda antar liga/pertandingan. Data yang tidak tersedia tidak boleh dianggap nol.
- Belum ada push notification, akun pengguna, backend, model prediksi buatan sendiri, atau live polling skor otomatis. V1 mengambil data saat pengguna menekan tombol.
- API-Football menyatakan livescore/fixture dapat diperbarui sekitar 15 detik, statistik fixture sekitar 1 menit, prediksi sekitar 1 jam, dan cedera sekitar 4 jam; pembaruan aktual dapat berbeda menurut kompetisi.

## Build lokal
Dengan Android SDK dan Gradle terpasang: `gradle assembleDebug`.
