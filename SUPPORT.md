# Aviso de suporte e atualizações

## Política

A partir de **04/11/2026 às 00h UTC**, versões abaixo da mínima declarada deixam de receber correções de segurança do proprietário

Antes dessa data o aviso informa que o suporte está prestes a terminar — depois informa que as atualizações de segurança terminaram

O texto explica o risco de falhas sem afirmar que o usuário ou o aplicativo já foi infectado

| Aplicativo | Versão publicada conferida em 01/11/2026 | Mínima |
| --- | --- | --- |
| WinSIMU | v0.1.0-alpha — prévia | 0.1.0 |

## Comportamento

- Detecta o idioma do sistema ou navegador — português, inglês, espanhol, francês, alemão, italiano, russo, chinês, japonês, coreano, árabe e hindi, com inglês como alternativa
- Compara versões numericamente e ignora sufixos de build quando o aplicativo só informa a série de versão
- Consulta somente dados públicos de suporte por HTTPS, com prazo limitado e catálogo local quando está sem internet
- Não envia arquivos, prompts, senhas, dados de hardware ou dados NFC
- Mostra a versão instalada e a mínima, com opções de atualizar, ver a tabela ou deixar para depois
- Abre a página oficial e preserva a decisão de baixar e instalar com o usuário

A tabela pública fica em [Apps e suporte](https://chimpi-hackintosh.github.io/ChimpiBlender-Online/support.html) depois que o PR do site for integrado e o Pages for publicado

## Distribuição e limites

**Alterar o repositório não altera binários já instalados** — versões antigas sem verificador precisam receber uma atualização manual inicial para passarem a consultar a política

Este PR entrega código e verificações — não equivale a um novo Release, atualização instalada ou site já implantado

As prévias continuam identificadas como prévias — os números da tabela vêm de lançamentos existentes e não foram inventados para esta alteração

O catálogo público não inclui repositórios privados

## Validação local desta alteração

Fonte original recuperado e SHA-256 conferido

Bootstrap executado com o arquivo original local e overlay aplicado

Android SDK/JDK indisponíveis no ambiente local — testes JVM e compilação do APK estão configurados no workflow do PR, sem publicar Release

Os testes do aviso cobrem versões iguais e mais novas sem alerta, versões antigas com alerta, mudança exata de data, traduções, falha de rede e rejeição de links não oficiais

## Construir com os fontes originais

`python scripts/prepare_support_build.py` baixa o ZIP original fixado em v0.1.0-alpha e confere o SHA-256 antes de extrair

O script recusa destino existente, caminhos externos e links simbólicos — aplica apenas os fontes em overlay/

Bibliotecas nativas e material de assinatura de desenvolvimento permanecem fora do git, na pasta build-source/ ignorada

O workflow compila um APK de depuração como artefato — não cria Release nem troca a assinatura por uma chave inventada
