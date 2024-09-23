/*
 * Copyright 2019 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.collect.android.activities.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.javarosa.core.io.BufferedInputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.formmanagement.ServerFormDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

import timber.log.Timber;

public class FormDownloadListViewModel extends ViewModel {

    // Stocke les détails des formulaires récupérés depuis le serveur, indexés par leur identifiant
    private HashMap<String, ServerFormDetails> formDetailsByFormId = new HashMap<>();

    /**
     * Liste des formulaires obtenus depuis la réponse formList. La liste contient des HashMap
     * représentant chaque formulaire à afficher dans l'interface utilisateur. Chaque HashMap
     * agit comme un objet DisplayableForm avec des valeurs pour chaque composant affiché.
     */
    private final ArrayList<HashMap<String, String>> formList = new ArrayList<>();

    // Ensemble de formulaires sélectionnés par l'utilisateur, indexés par leur identifiant
    private final LinkedHashSet<String> selectedFormIds = new LinkedHashSet<>();

    // Variables pour gérer l'état des boîtes de dialogue d'alerte
    private String alertTitle;
    private String alertDialogMsg;

    // Indicateurs de l'état des boîtes de dialogue
    private boolean alertShowing;
    private boolean cancelDialogShowing;
    private boolean shouldExit;
    private boolean loadingCanceled;

    // Variables utilisées lorsque l'activité est appelée depuis une application externe
    private boolean isDownloadOnlyMode;
    private String[] formIdsToDownload;
    private String url;
    private String username;
    private String password;

    // Stocke les résultats du téléchargement des formulaires, indexés par leur identifiant
    private final HashMap<String, Boolean> formResults = new HashMap<>();

    // Retourne la map des détails des formulaires par identifiant
    public HashMap<String, ServerFormDetails> getFormDetailsByFormId() {
        return formDetailsByFormId;
    }

    // Définit la map des détails des formulaires par identifiant
    public void setFormDetailsByFormId(HashMap<String, ServerFormDetails> formDetailsByFormId) {
        this.formDetailsByFormId = formDetailsByFormId;
    }

    // Vide la map des détails des formulaires
    public void clearFormDetailsByFormId() {
        formDetailsByFormId.clear();
    }

    // Retourne le titre de l'alerte actuelle
    public String getAlertTitle() {
        return alertTitle;
    }

    // Définit le titre de l'alerte
    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }

    // Retourne le message de la boîte de dialogue d'alerte actuelle
    public String getAlertDialogMsg() {
        return alertDialogMsg;
    }

    // Définit le message de la boîte de dialogue d'alerte
    public void setAlertDialogMsg(String alertDialogMsg) {
        this.alertDialogMsg = alertDialogMsg;
    }

    // Indique si une alerte est actuellement affichée
    public boolean isAlertShowing() {
        return alertShowing;
    }

    // Définit si une alerte doit être affichée
    public void setAlertShowing(boolean alertShowing) {
        this.alertShowing = alertShowing;
    }

    // Indique si l'activité doit se terminer
    public boolean shouldExit() {
        return shouldExit;
    }

    // Définit si l'activité doit se terminer
    public void setShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }

    // Retourne la liste des formulaires à afficher
    public ArrayList<HashMap<String, String>> getFormList() {
        fetchProjectsFromServer();
        return formList;
    }

    // Vide la liste des formulaires
    public void clearFormList() {
        formList.clear();
    }

    // Ajoute un formulaire à la liste
    public void addForm(HashMap<String, String> item) {
        formList.add(item);
    }

    // Ajoute un formulaire à la liste à une position spécifique
    public void addForm(int index, HashMap<String, String> item) {
        formList.add(index, item);
    }

    // Retourne l'ensemble des identifiants de formulaires sélectionnés
    public LinkedHashSet<String> getSelectedFormIds() {
        return selectedFormIds;
    }

    // Ajoute un identifiant de formulaire à l'ensemble des formulaires sélectionnés
    public void addSelectedFormId(String selectedFormId) {
        selectedFormIds.add(selectedFormId);
    }

    // Retire un identifiant de formulaire de l'ensemble des formulaires sélectionnés
    public void removeSelectedFormId(String selectedFormId) {
        selectedFormIds.remove(selectedFormId);
    }

    // Vide l'ensemble des formulaires sélectionnés
    public void clearSelectedFormIds() {
        selectedFormIds.clear();
    }

    // Indique si le mode "Téléchargement uniquement" est activé
    public boolean isDownloadOnlyMode() {
        return isDownloadOnlyMode;
    }

    // Définit le mode "Téléchargement uniquement"
    public void setDownloadOnlyMode(boolean downloadOnlyMode) {
        isDownloadOnlyMode = downloadOnlyMode;
    }

    // Retourne les résultats du téléchargement des formulaires
    public HashMap<String, Boolean> getFormResults() {
        checkServerConnection();
        return formResults;
    }

    // Enregistre le résultat du téléchargement d'un formulaire
    public void putFormResult(String formId, boolean result) {
        formResults.put(formId, result);
    }

    // Retourne le mot de passe utilisé pour l'authentification
    public String getPassword() {
        return password;
    }

    // Définit le mot de passe utilisé pour l'authentification
    public void setPassword(String password) {
        this.password = password;
    }

    // Retourne le nom d'utilisateur utilisé pour l'authentification
    public String getUsername() {
        return username;
    }

    // Définit le nom d'utilisateur utilisé pour l'authentification
    public void setUsername(String username) {
        this.username = username;
    }

    // Retourne l'URL du serveur à partir duquel les formulaires doivent être téléchargés
    public String getUrl() {
        return url;
    }

    // Définit l'URL du serveur à partir duquel les formulaires doivent être téléchargés
    public void setUrl(String url) {
        this.url = url;
    }

    // Retourne les identifiants des formulaires à télécharger
    public String[] getFormIdsToDownload() {
        return Arrays.copyOf(formIdsToDownload, formIdsToDownload.length);
    }

    // Définit les identifiants des formulaires à télécharger
    public void setFormIdsToDownload(String[] formIdsToDownload) {
        this.formIdsToDownload = formIdsToDownload;
    }

    // Indique si la boîte de dialogue d'annulation est affichée
    public boolean isCancelDialogShowing() {
        return cancelDialogShowing;
    }

    // Définit si la boîte de dialogue d'annulation doit être affichée
    public void setCancelDialogShowing(boolean cancelDialogShowing) {
        this.cancelDialogShowing = cancelDialogShowing;
    }

    // Indique si le chargement a été annulé
    public boolean wasLoadingCanceled() {
        return loadingCanceled;
    }

    // Définit si le chargement a été annulé
    public void setLoadingCanceled(boolean loadingCanceled) {
        this.loadingCanceled = loadingCanceled;
    }

    // Classe Factory pour créer des instances de FormDownloadListViewModel
    public static class Factory implements ViewModelProvider.Factory {

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new FormDownloadListViewModel();
        }
    }





    // Autres membres et méthodes de la classe...

    public void checkServerConnection() {
        new Thread(() -> {
            try {
                // Vérifier la connexion au serveur
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    // Si nous arrivons ici, la connexion est réussie
                    // Vous pouvez récupérer les projets à ce moment-là
                    fetchProjectsFromServer();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                // Gérer les exceptions liées à la connexion
                e.printStackTrace();
            }
        }).start();
    }

    private void fetchProjectsFromServer() {
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            try {
                // Exemple de requête pour récupérer les projets
                URL urlObject = new URL("https://kta.kalico.bi/en/");
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Timber.e("Erreur lors de la connexion au serveur : Code de réponse %d", responseCode);
                    return; // Quitter la méthode en cas d'erreur de connexion
                }

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Donnnees "+result);
                    result.append(line);
                }

                // Parsez le résultat JSON pour extraire les projets
                JSONObject jsonResponse;
                try {
                    jsonResponse = new JSONObject(result.toString());
                } catch (JSONException e) {
                    Timber.e(e, "Erreur lors du parsing du JSON");
                    return; // Quitter la méthode en cas d'erreur de parsing
                }

                JSONArray projects;
                try {
                    projects = jsonResponse.getJSONArray("projects");
                } catch (JSONException e) {
                    Timber.e(e, "Erreur lors de la récupération des projets depuis la réponse JSON");
                    return; // Quitter la méthode en cas d'erreur d'extraction des projets
                }

                // Convertir les projets JSON en objets Project
                ArrayList<Project> projectsList = new ArrayList<>();
                for (int i = 0; i < projects.length(); i++) {
                    JSONObject projectJson;
                    try {
                        projectJson = projects.getJSONObject(i);
                    } catch (JSONException e) {
                        Timber.e(e, "Erreur lors de la récupération d'un projet à l'index %d", i);
                        continue; // Passer au projet suivant en cas d'erreur pour ce projet
                    }

                    Project project = new Project();
                    try {
                        project.setId(projectJson.getString("id"));
                        project.setName(projectJson.getString("name"));
                    } catch (JSONException e) {
                        Timber.e(e, "Erreur lors de la récupération des détails du projet à l'index %d", i);
                        continue; // Passer au projet suivant en cas d'erreur pour ce projet
                    }
                    projectsList.add(project);
                }

                // Afficher les projets dans la console en utilisant Timber
                Timber.d("Liste des projets récupérés :");
                for (Project project : projectsList) {
                    Timber.d("ID: %s, Nom: %s", project.getId(), project.getName());
                }

            /*// Mettre à jour les données de votre ViewModel avec les projets
            setProjectsList(projectsList);*/

            } catch (MalformedURLException e) {
                Timber.e(e, "URL mal formée : %s",  "/projects");
            } catch (IOException e) {
                Timber.e(e, "Erreur lors de la connexion ou de la lecture des données");
            } catch (Exception e) {
                Timber.e(e, "Erreur inattendue lors de la récupération des projets");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }).start();
    }



    // Exemple de classe Project
    public static class Project {
        private String id;
        private String name;

        // Getters et Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
