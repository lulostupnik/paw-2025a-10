import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import type { ProfileDetail } from "@/types/profile";
import { classNames } from "@/lib/utils/classNames";

interface ProfileHeaderProps {
    profile: ProfileDetail;
}

const getInitials = (firstname: string, lastname: string) =>
    [firstname, lastname]
        .filter(Boolean)
        .map((part) => part[0])
        .join("")
        .toUpperCase();

export default function ProfileHeader({ profile }: ProfileHeaderProps) {
    const { t } = useI18n();
    const [menuOpen, setMenuOpen] = useState(false);
    const initials = useMemo(() => getInitials(profile.firstname, profile.lastname), [profile.firstname, profile.lastname]);

    return (
        <div className="profile-header">
            <div className="profile-avatar-container">
                <div className="profiles-avatar">
                    {profile.links?.profilePictureUrl ? (
                        <img src={profile.links.profilePictureUrl} alt={profile.username} className="avatar-image" />
                    ) : (
                        <div className="avatar-placeholder">{initials}</div>
                    )}

                    {profile.isMine && (
                        <Link to="/profiles/me/edit-picture" className="avatar-edit-overlay" aria-label={t("profile.edit.picture.button")}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path>
                                <circle cx="12" cy="13" r="4"></circle>
                            </svg>
                        </Link>
                    )}
                </div>
            </div>

            <div className="profile-info">
                <h1 className="profile-name">{profile.firstname} {profile.lastname}</h1>
                <p className="profile-username">@{profile.username}</p>
            </div>

            {profile.isMine && (
                <div className="profile-actions">
                    <div className="action-buttons-group">
                        <Link to="/profiles/me/edit" className="btn-primary btn-edit-info">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                            </svg>
                            <span className="btn-text">{t("profile.edit")}</span>
                        </Link>

                        <Link to="/profiles/me/change-password" className="btn-secondary btn-change-password">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                                <circle cx="12" cy="16" r="1"></circle>
                                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                            </svg>
                            <span className="btn-text">{t("profile.edit.password")}</span>
                        </Link>
                    </div>

                    <div className="profile-actions-dropdown">
                        <button
                            type="button"
                            className="btn-dropdown-toggle"
                            onClick={() => setMenuOpen((prev) => !prev)}
                            aria-haspopup="menu"
                            aria-expanded={menuOpen}
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <circle cx="12" cy="12" r="1"></circle>
                                <circle cx="12" cy="5" r="1"></circle>
                                <circle cx="12" cy="19" r="1"></circle>
                            </svg>
                        </button>

                        <div className={classNames("dropdown-menu", menuOpen && "show")} role="menu">
                            <Link to="/profiles/me/edit-picture" className="dropdown-item">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path>
                                    <circle cx="12" cy="13" r="4"></circle>
                                </svg>
                                {t("profile.edit.picture.button")}
                            </Link>

                            <Link to="/profiles/me/edit" className="dropdown-item">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                </svg>
                                {t("profile.edit.button")}
                            </Link>

                            <Link to="/profiles/me/change-password" className="dropdown-item">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                                    <circle cx="12" cy="16" r="1"></circle>
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                                </svg>
                                {t("profile.edit.password")}
                            </Link>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
