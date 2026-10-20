# WinSIMU · 0.1.0-alpha

Emulador de PC x86 para Android, derivado do **Limbo 6.0.1**, com motor
**QEMU 5.1.0**. Executa instruções de uma máquina real emulada: o Windows deve
ser instalado na máquina virtual para executar seus programas `.exe`

**Esta é uma primeira alpha, não uma garantia de suporte do Windows 1.0 ao 11**
Há 21 perfis iniciais, mas nenhum Windows foi validado em um celular físico
para esta compilação. O Windows 11 não tem instalação padrão integrada nesta
versão, pois ainda faltam a configuração UEFI e o TPM virtual

## Downloads

- [Baixar APK WinSIMU 0.1.0-alpha](https://github.com/Chimpi-hackintosh/WinSIMU/raw/refs/heads/main/WinSIMU-0.1.0-alpha.apk)
- [Baixar os fontes completos](https://github.com/Chimpi-hackintosh/WinSIMU/raw/refs/heads/main/WinSIMU-0.1.0-alpha-source.zip)

O APK é uma compilação de desenvolvimento. O ZIP contém o projeto Android,
as bibliotecas do motor e os fontes das dependências nativas

## Começar pelo APK

1. Instale o APK de desenvolvimento do WinSIMU no Android 8.0 ou posterior
2. Abra **Testar motor (sem Windows)** e depois pressione **Play** na tela do motor
3. O disquete de diagnóstico deve mostrar `WinSIMU: real x86 boot OK!`
4. Volte à tela inicial e escolha **Criar máquina** para configurar seu Windows

O diagnóstico executa código x86 de 16 bits pelo QEMU e escreve pela BIOS
O fonte está em `diagnostics/boot.asm`; ele não inclui DOS nem Windows

## Instalar Windows e executar programas

1. Escolha um perfil e um nome para a máquina
2. Defina RAM e disco virtual, reservando recursos para o Android
3. Selecione sua ISO de instalação ou imagem de disquete
4. Marque a opção de disquete somente quando a mídia realmente for um disquete
5. Salve, confira a configuração na tela do motor e pressione **Play**
6. Instale o sistema pelo instalador original, dentro do PC emulado
7. Depois da instalação, altere **Boot from Device** para **Hard Disk**
8. Instale aplicativos compatíveis com esse Windows usando uma mídia virtual
   adicional ou uma imagem de disco que contenha seus instaladores

Windows 1.x, 2.x e 3.x precisam primeiro de uma instalação compatível de
MS-DOS. As mídias desses sistemas são fornecidas pelo usuário

O APK não abre diretamente qualquer `.exe` escolhido no Android. Os programas
são executados **dentro do Windows convidado**, após sua instalação

## Perfis iniciais

Os valores abaixo são configurações de partida, não mínimos oficiais nem
resultados de testes de compatibilidade

| Perfil | RAM inicial | Disco virtual inicial |
| --- | ---: | ---: |
| Windows 1.0, 2.0 e 2.1 | 16 MiB | 64 MiB |
| Windows 3.0 e 3.1 | 16 MiB | 128 MiB |
| Windows 3.11 | 32 MiB | 512 MiB |
| Windows 95 | 32 MiB | 512 MiB |
| Windows 98 | 64 MiB | 4 GiB |
| Windows Me | 128 MiB | 4 GiB |
| Windows NT 3.1 e NT 3.5 | 32 MiB | 512 MiB |
| Windows NT 3.51 | 64 MiB | 1 GiB |
| Windows NT 4.0 | 64 MiB | 4 GiB |
| Windows 2000 | 256 MiB | 4 GiB |
| Windows XP, 32 bits | 512 MiB | 16 GiB |
| Windows Vista e 7, 32 bits | 1024 MiB | 32 GiB |
| Windows 8 e 8.1, 64 bits | 2048 MiB | 32 GiB |
| Windows 10, 64 bits | 2048 MiB | 64 GiB |
| Windows 11, 64 bits | 4096 MiB | Somente disco já instalado nesta alpha |

Windows NT 3.x pode precisar de ajustes adicionais de CPU e mídia
Vista e posteriores são experimentais em celulares. Não há aceleração
Direct3D; desempenho e compatibilidade de aplicativos variam

O perfil do Windows 11 aceita apenas testes com um disco existente, processo
Android de 64 bits e pelo menos 4096 MiB atribuídos à VM. Isso não comprova
boot nem atendimento de todos os requisitos do sistema

## Arquivos e discos

- O assistente usa o seletor de documentos do Android e guarda as permissões
  de acesso aos arquivos selecionados
- Imagens de disco importadas precisam permitir escrita e acesso aleatório
- ISOs e disquetes são conectados sem copiar arquivos grandes para o aplicativo
- Novos discos são RAW esparsos: a capacidade virtual não equivale ao espaço
  ocupado imediatamente, mas as gravações precisam de armazenamento físico
- Novos discos ficam no armazenamento privado do aplicativo, em `files/disks/`
  e são apagados ao desinstalar ou limpar os dados do WinSIMU
- Para preservar um disco novo, copie-o antes de desinstalar. Nesta alpha, isso
  exige uma ferramenta de desenvolvimento, como `adb run-as` com o APK debug
- Para manter o controle direto do arquivo, prefira importar uma imagem de
  disco existente guardada fora dos dados privados do aplicativo
- Segurar um cartão da lista permite remover sua configuração sem apagar o disco
- Emulador e instalador do Windows podem modificar os discos importados

## Compilar no Android Studio

Baixe e extraia o ZIP de fontes acima e abra a pasta `WinSIMU/` como projeto. Use **JDK 17**, Android SDK Platform 35 e Build
Tools 35.0.0. O projeto usa AGP 8.9.2 e Gradle 8.13

As bibliotecas nativas para `armeabi-v7a`, `arm64-v8a`, `x86` e `x86_64` já
acompanham o projeto. Não é necessário instalar o NDK para recompilar a UI

```bash
./gradlew :limbo-android-lib:testDebugUnitTest :limbo-android-x86:assembleDebug
```

No Windows, use `gradlew.bat` no lugar de `./gradlew`

O APK usa a chave de desenvolvimento em `signing/winsimu-dev.keystore`, com
alias `androiddebugkey` e senha `android`. Ela acompanha os fontes para manter
a assinatura entre compilações de teste. Para uma publicação, configure uma
chave privada de lançamento separada

Saída: `limbo-android-x86/build/outputs/apk/debug/limbo-android-x86-debug.apk`

Se as bibliotecas nativas forem removidas do projeto, execute primeiro:

```bash
python3 scripts/bootstrap_engine.py
python3 scripts/generate_diagnostic.py
```

O script valida o SHA-256 do APK oficial de origem antes de extrair o motor
A compilação falha explicitamente quando o motor ou a BIOS não estão presentes

O ZIP de fontes também inclui `.github/workflows/build-apk.yml`. Ao extrair
o projeto e versionar seus arquivos, esse workflow pode compilar o APK pela
aba Actions. Neste repositório, o aplicativo e seus fontes completos são
publicados como arquivos para download

## Fontes e recompilação do motor

As fontes completas das dependências estão comprimidas em `native-sources/`
Para extraí-las e aplicar os patches do Limbo, em Linux com Python 3.12+:

```bash
python3 scripts/prepare_native.py
```

Consulte `README.developers` para a compilação nativa original com NDK r23b,
clang e os utilitários de compilação Linux. As regras estão em
`limbo-android-lib/src/main/jni/`. Essa recompilação nativa não é necessária
para gerar o APK com os binários incluídos

## Estrutura principal

| Local | Responsabilidade |
| --- | --- |
| `limbo-android-x86/` | Aplicativo, tela inicial, assistente, diagnóstico e binários nativos |
| `limbo-android-lib/` | Interface do motor, SDL, JNI, armazenamento das máquinas e controles |
| `WindowsProfile.java` | Catálogo dos 21 perfis e validação de seus valores |
| `WinSimuMachines.java` | Criação persistente da configuração e dos novos discos |
| `scripts/` | Recuperação do motor, preparo de fontes e geração do diagnóstico |
| `native-sources/` | Fontes originais do QEMU, firmwares e bibliotecas |
| `VALIDACAO.md` | Evidências da compilação e limites dos testes realizados |

## Estado e próximas etapas

A tela inicial e o assistente usam português ou inglês conforme o sistema
Parte das telas herdadas do motor permanece em inglês. O alvo Android 29 é
mantido para compatibilidade com o gerenciador de arquivos legado; a migração
completa desse fluxo é necessária antes de uma distribuição moderna na Play Store

Próximas etapas: testar uma instalação antiga no celular, validar mídia e
drivers por versão, adicionar exportação de discos na interface e implementar
uma configuração UEFI/TPM antes de oferecer instalação padrão do Windows 11

## Créditos e licença

WinSIMU é um derivado do **Limbo Emulator**, de Max Kastanas e colaboradores,
tag `v6.0.1-LimboEmulator`, commit `887c6a68cd6b414377d7f8071bc12bbc16e59809`

As adições WinSIMU e o Limbo usam GPL-2.0-or-later. O texto original está em
`COPYING`. QEMU, SDL2, GLib, libffi, pixman e firmwares mantêm seus autores e
licenças próprios, presentes nos respectivos arquivos-fonte

- Limbo: https://github.com/limboemu/limbo
- QEMU: https://www.qemu.org
- Requisitos do Windows 11: https://learn.microsoft.com/pt-br/windows/whats-new/windows-11-requirements

Este projeto não é um produto da Microsoft e não inclui mídias ou chaves do Windows
