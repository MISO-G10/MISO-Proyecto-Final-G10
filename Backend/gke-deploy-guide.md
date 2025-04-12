
Guía para Desplegar Microservicios en GKE Autopilot con GitHub Actions

## 1. Crear un proyecto en Google Cloud

1. Ve a: https://console.cloud.google.com/
2. Haz clic en el selector de proyectos → "Nuevo Proyecto".
3. Dale un nombre (ej: `ccp-microservicios`) y toma nota del ID del proyecto (ej: `ccp-microservicios-123456`).

---

## 2. Activar APIs necesarias

```bash
gcloud services enable \
  container.googleapis.com \
  containerregistry.googleapis.com \
  compute.googleapis.com \
  cloudresourcemanager.googleapis.com \
  iam.googleapis.com
```

---

## 3. Crear un clúster GKE Autopilot

Desde la consola:

1. Ir a **Kubernetes Engine > Clusters**
2. Clic en **"Crear clúster"**
3. Elige **Autopilot**
4. Asigna un nombre, por ejemplo `ccp-cluster`
5. Selecciona una región (ej: `us-central1`)
6. Crear el clúster (toma unos minutos)

---

## 4. Reservar una IP externa estática

1. Ir a **VPC network > External IP addresses**
2. Clic en **"RESERVAR IP ESTÁTICA"**
3. Nombre: `ccp-ingress-ip`
4. Tipo: **regional**, en la misma región del clúster
5. Guarda y copia la IP

---

## 5. Crear una cuenta de servicio para GitHub Actions

```bash
gcloud iam service-accounts create github-deploy \
  --display-name "GitHub Actions Deploy"
```

Otorgar permisos necesarios:

```bash
gcloud projects add-iam-policy-binding PROJECT_ID \
  --member="serviceAccount:github-deploy@PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/container.admin"

gcloud projects add-iam-policy-binding PROJECT_ID \
  --member="serviceAccount:github-deploy@PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/storage.admin"
```

---

## 6. Crear clave JSON y subirla a GitHub

```bash
gcloud iam service-accounts keys create ~/github-key.json \
  --iam-account=github-deploy@PROJECT_ID.iam.gserviceaccount.com
```

1. Abre el archivo `~/github-key.json` y copia su contenido.
2. Ve a tu repositorio en GitHub → Settings → Secrets and variables → Actions → **New Repository Secret**
3. Crea estos secretos:

| Nombre                 | Valor                                                  |
|------------------------|--------------------------------------------------------|
| `GCP_PROJECT`          | ID del proyecto (ej. `ccp-microservicios-123456`)      |
| `GKE_CLUSTER_NAME`     | Nombre del clúster (ej. `ccp-cluster`)                 |
| `GKE_CLUSTER_LOCATION` | Región (ej. `us-central1`)                             |
| `GCP_SA_KEY`           | Contenido del archivo `github-key.json`               |

---

## 7. Instalar `gcloud` y `kubectl` localmente (opcional)

```bash
gcloud components install gke-gcloud-auth-plugin
gcloud components install kubectl
gcloud container clusters get-credentials ccp-cluster --region us-central1
```

---

## 8. ¡Listo para desplegar!

- Haz `push` a la rama `master`
- GitHub Actions construirá, subirá y aplicará todo en GKE
- Monitorea en GCP → Kubernetes Engine → Workloads

---
