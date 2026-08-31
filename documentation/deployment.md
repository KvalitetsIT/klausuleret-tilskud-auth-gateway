# Idriftsættelse

Komponenten findes som image på Dockerhub: [kvalitetsit/klausuleret-tilskud-auth-gateway](https://hub.docker.com/r/kvalitetsit/klausuleret-tilskud-auth-gateway).

Den idriftsættes som en OIOSAML service provider med SEB som identity provider. 
Hertil kræves et key store, som benyttes til at signere SAML forespørgsler.

Konfigurationen er beskrevet her: [Konfiguration](configuration.md)

Herefter vil komponenten være tilgængelig på port 8080.

Efter idriftsættelse, laves en tilslutningsaftale med SEB, hvor følgende skal angives:
- URL til servicen
- Logout URL (`<URL> + /saml/logout`)
- Metadata URL (`<URL> + /saml/metadata`)
