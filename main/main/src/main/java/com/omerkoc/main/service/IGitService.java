package com.omerkoc.main.service;

import java.nio.file.Path;

public interface IGitService {
    // 1. Repoyu klonlar ve temp klasörün yolunu döner
    // string dönmez obje olarka döner
    // kopyalanan projenin geçici klasör yolunu döndürür
    Path cloneRepository(String repoUrl);

    // 2. Temp klasöründeki kod dosyalarını tarayıp tek bir metin haline getirir
    // yoldan gelen dosyaları text e çevirir
    String mergeFilesToText(Path tempDir);

    // 3. Geçici klasörü diskten siler
    void cleanUp(Path tempDir);
}
