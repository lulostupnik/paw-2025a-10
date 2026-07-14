import { useEffect, useState, type FormEvent } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { useProfileDetail } from "@/hooks/profiles/useProfileDetail";
import { useProfileUpsert } from "@/hooks/profiles/useProfileUpsert";
import PageStatus from "@/components/ui/PageStatus";
import AvatarFallbackIcon from "@/components/ui/AvatarFallbackIcon";
import { useToast } from "@/components/ui/ToastProvider";
import { apiErrorMessage } from "@/lib/api/client";
import { sanitizeInternalPath } from "@/lib/utils/internalPath";

export default function ProfilePictureForm() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const location = useLocation();
    const { showToast } = useToast();
    const { data: profile, isLoading, isError } = useProfileDetail("me");
    const { updatePicture, isLoading: isSaving } = useProfileUpsert();
    // La preview vive junto al archivo que la origina: así el object URL se crea
    // en el handler y no hace falta sincronizar dos estados con un efecto.
    const [picture, setPicture] = useState<{ file: File; previewUrl: string } | null>(null);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const returnPath = sanitizeInternalPath((location.state as { from?: string } | null)?.from);

    useEffect(() => {
        if (!picture) {
            return;
        }
        const { previewUrl } = picture;
        return () => URL.revokeObjectURL(previewUrl);
    }, [picture]);

    const handleFileChange = (nextFile: File | null) => {
        setPicture(nextFile ? { file: nextFile, previewUrl: URL.createObjectURL(nextFile) } : null);
    };

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setSubmitError(null);
        if (!picture) {
            setSubmitError(t("ImageNotEmpty.editPictureForm.picture"));
            return;
        }
        try {
            await updatePicture({ picture: picture.file });
            showToast(t("profile.toast.updated"), { variant: "success" });
            navigate(returnPath ?? "/profiles/me/info", { replace: true });
        } catch (error) {
            setSubmitError(
                apiErrorMessage(
                    error,
                    t("admin.dashboard.error", { defaultValue: "Error guardando los cambios." })
                )
            );
        }
    };

    if (isLoading) {
        return <PageStatus className="profile-form-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError || !profile) {
        return (
            <div className="profile-form-page">
                <div className="error-container">{t("profile.not.found")}</div>
            </div>
        );
    }

    return (
        <div className="profile-form-page">
            <div className="auth-container">
                <div className="auth-card profile-edit-card">
                    <div className="auth-header">
                        <div className="auth-logo">
                            <svg xmlns="http://www.w3.org/2000/svg" className="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 0 0-7 7h14a7 7 0 0 0-7-7z" />
                            </svg>
                        </div>
                        <h1 className="auth-title">{t("profile.edit.picture.title", { defaultValue: "Editar Perfil" })}</h1>
                        <p className="auth-subtitle">{t("profile.edit.picture.subtitle", { defaultValue: "Actualiza la informacion de tu perfil" })}</p>
                    </div>

                    <form className="auth-form profile-edit-form" onSubmit={handleSubmit} noValidate>
                        <div className="profile-picture-section">
                            <div className="current-avatar-container">
                                <div className="current-avatar" id="currentAvatar">
                                    {picture || profile.links?.profilePictureUrl ? (
                                        <img
                                            src={picture?.previewUrl ?? profile.links?.profilePictureUrl ?? ""}
                                            alt={profile.username}
                                            className="avatar-image current-avatar-image"
                                            id="avatarPreview"
                                        />
                                    ) : (
                                        <div className="avatar-placeholder" id="avatarPlaceholder">
                                            <AvatarFallbackIcon size={42} />
                                        </div>
                                    )}
                                </div>
                            </div>

                            <div className="form-group">
                                <label htmlFor="profilePicture" className="form-label">
                                    {t("profile.picture")}
                                </label>
                                <div className="file-upload">
                                    <label className="file-upload-label">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="file-upload-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                        </svg>
                                        <span className="file-upload-text">{t("upload_picture.profile")}</span>
                                        <span className="file-upload-hint">{t("upload_picture.hint", { defaultValue: "JPG o PNG, max 5MB" })}</span>
                                        <input
                                            id="profilePicture"
                                            type="file"
                                            className="file-upload-input"
                                            accept="image/png, image/jpeg"
                                            onChange={(event) => handleFileChange(event.target.files?.[0] ?? null)}
                                        />
                                    </label>
                                </div>
                                {picture && (
                                    <div className="file-preview">
                                        <img
                                            src={picture.previewUrl}
                                            className="file-preview-image"
                                            alt={t("profile.picture.preview.alt")}
                                        />
                                        <span className="file-preview-name">{picture.file.name}</span>
                                        <button
                                            type="button"
                                            className="file-preview-remove"
                                            aria-label={t("common.removeFile")}
                                            onClick={() => handleFileChange(null)}
                                        >
                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                                <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z" />
                                            </svg>
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                        {submitError && <p className="error-message">{submitError}</p>}

                        <div className="form-actions">
                            <button type="submit" className="form-button" disabled={isSaving}>
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                                    <polyline points="17,21 17,13 7,13 7,21"></polyline>
                                    <polyline points="7,3 7,8 15,8"></polyline>
                                </svg>
                                {t("profile.save.changes")}
                            </button>
                        </div>
                    </form>

                    <div className="auth-footer">
                        <Link to={returnPath ?? "/profiles/me/info"} className="auth-link">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M19 12H5"></path>
                                <path d="M12 19l-7-7 7-7"></path>
                            </svg>
                            {t("profile.back.to.profile")}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}
