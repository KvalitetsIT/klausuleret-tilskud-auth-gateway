# klausuleret-tilskud-auth-gateway

![Build Status](https://github.com/KvalitetsIT/klausuleret-tilskud-auth-gateway/workflows/CICD/badge.svg)

---

Dette komponent fungerer som en gateway for autentificering og autorisation 
af forespørgsler til valideringskomponenten i Klausuleret Tilskud.

Efter succesfuldt login, vil gateway'en videresende kald til valideringskomponenten, 
hvor brugerens id tilknyttes i headeren 'User-ID'.


Komponenten benytter sig af [OIOSAML.java](https://github.com/digst/OIOSAML.Java) biblioteket
til håndtering af SAML-baseret autentificering og autorisation.

## Oversigt
- [Konfiguration](./documentation/configuration.md)
- [Idriftsættelse](./documentation/deployment.md)
