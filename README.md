# klausuleret-tilskud-auth-gateway

![Build Status](https://github.com/KvalitetsIT/klausuleret-tilskud-auth-gateway/workflows/CICD/badge.svg)

**Komponent:** Klausuleret Tilskud – Auth Gateway  
**Version:** 0.0.1  
**Dato:** 15. januar 2026  
**Udarbejdet af:** KvalitetsIT

---

Dette komponent fungerer som en gateway for autentificering og autorisation 
af forespørgsler til valideringskomponenten i Klausuleret Tilskud.

Efter succesfuldt login, vil kald til valideringskomponenten få tilknyttet brugerens id i headeren 'User-ID'.


Komponenten benytter sig af [OIOSAML.java](https://github.com/digst/OIOSAML.Java) biblioteket
til håndtering af SAML-baseret autentificering og autorisation.

# Konfiguration

De følgende tabeller indeholder miljø-variable og dertilhørende beskrivelser.

## Applikationsspecifikke variable

| Environment variable              | Description                                                                                                                         | Required |
|-----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|----------|
| LOG_LEVEL                         | Logniveau for applikations-log. Standardværdi er INFO.                                                                              | Nej      |
| LOG_LEVEL_FRAMEWORK               | Logniveau for framework. Standardværdi er INFO.                                                                                     | Nej      |
| CORRELATION_ID                    | HTTP-header, der angiver hvilken correlation id der skal bruges. Bruges til at korrelere logbeskeder. Standard er "x-request-id".   | Nej      |
| SPRING_PROFILES_ACTIVE            | Sæt denne til 'without-oiosaml' for at køre uden OIOSAML. Forespørgsler vil få tilknyttet bruger id'et 'mocked-user'. Kun til test. | Nej      |
| ITUKT_GATEWAY_ALLOWEDORIGINS      | En liste af URL’er/origins som skal tillades af CORS.                                                                               | Nej      |
| ITUKT_GATEWAY_API_URL             | URL på det api der skal forwardes til.                                                                                              | Ja       |

## OIOSAML servlet variable

Følgende variable bruges til at konfigurere OIOSAML servletten. Se evt dokumentationen af OIOSAML.java for flere detaljer.

| Environment variable                            | Description                                                      | Required |
|-------------------------------------------------|------------------------------------------------------------------|----------|
| ITUKT_GATEWAY_OIOSAML_SERVLET_ENTITYID          | ID der identificerer applikationen som OIOSAML service provider. | Ja       |
| ITUKT_GATEWAY_OIOSAML_SERVLET_BASEURL           | Den URL applikationen er tilgængelig på.                         | Ja       |
| ITUKT_GATEWAY_OIOSAML_SERVLET_KEYSTORE_LOCATION | Sti til det keystore der skal benyttes af servletten.            | Ja       |
| ITUKT_GATEWAY_OIOSAML_SERVLET_KEYSTORE_PASSWORD | Kodeord til ovenstående keystore.                                | Ja       |
| ITUKT_GATEWAY_OIOSAML_SERVLET_KEYSTORE_ALIAS    | Alias på keyentry i ovenstående keystore.                        | Ja       |
| ITUKT_GATEWAY_OIOSAML_SERVLET_IDP_ENTITYID      | Entity ID på den idP, der skal benyttes til login.               | Ja       |
| ITUKT_GATEWAY_OIOSAML_SERVLET_IDP_METADATAFILE  | Sti til metadata fil for idP'en                                  | Ja       |
