import { Link } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import AdminPagination from "@/components/admin-dashboard/AdminPagination";
import type { ProfileInterest } from "@/types/profile";

interface ProfileInterestsTabProps {
    interests: ProfileInterest[];
    isMine: boolean;
    page: number;
    totalPages: number;
    pageSize: number;
    onPageChange: (page: number) => void;
}

export default function ProfileInterestsTab({
    interests,
    isMine,
    page,
    totalPages,
    pageSize,
    onPageChange,
}: ProfileInterestsTabProps) {
    const { t } = useI18n();

    return (
        <div className="profile-section active" id="interests-section">
            <div className="profile-card">
                <h2 className="section-title">{t("profile.home.interest")}</h2>
                <div className="info-list">
                    {interests.length > 0 ? (
                        <>
                            {interests.map((interest) => (
                                <div key={interest.id} className="info-item">
                                    <p className="info-value">{interest.name}</p>
                                </div>
                            ))}

                            <AdminPagination
                                totalPages={totalPages}
                                currentPage={page}
                                pageSize={pageSize}
                                onPageChange={onPageChange}
                                previousLabel={t("pagination.prev")}
                                nextLabel={t("pagination.next")}
                            />

                            {isMine && (
                                <div className="action-buttons">
                                    <Link to="/profiles/me/interests/edit" className="btn-primary">
                                        {t("profile.edit.interests")}
                                    </Link>
                                </div>
                            )}
                        </>
                    ) : (
                        <div className="empty-state">
                            <div className="empty-icon">
                                <svg xmlns="http://www.w3.org/2000/svg" className="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                                </svg>
                            </div>
                            <p className="empty-message">{t("profile.no.interests")}</p>

                            {isMine && (
                                <div className="action-buttons">
                                    <Link to="/profiles/me/interests/edit" className="btn-primary">
                                        {t("profile.add.interests")}
                                    </Link>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
