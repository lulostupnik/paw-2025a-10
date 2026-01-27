import { Link } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import Pagination from "@/components/listing/Pagination";
import type { ProfileInterest } from "@/types/profile";
import type { PageResult } from "@/types/pagination";

interface ProfileInterestsTabProps {
    isMine: boolean;
    page: PageResult<ProfileInterest>
    onPageChange: (page: number | string) => void;
}

export default function ProfileInterestsTab({
    isMine, 
    page,
    onPageChange,
}: ProfileInterestsTabProps) {
    const { t } = useI18n();

    return (
        <div className="profile-section active" id="interests-section">
            <div className="profile-card">
                <h2 className="section-title">{t("profile.home.interest")}</h2>
                <div className="info-list">
                    {page.content?.length > 0 ? (
                        <>
                            {page.content.map((interest) => (
                                <div key={interest.id} className="info-item">
                                    <p className="info-value">{interest.name}</p>
                                </div>
                            ))}

                            <Pagination
                                totalPages={page.totalPages}
                                currentPage={page.currentPage}
                                pageSize={page.pageSize}
                                onPageChange={onPageChange}
                                previousLabel={t("pagination.prev")}
                                nextLabel={t("pagination.next")}
                                nextPage={page.next}
                                prevPage={page.prev}
                                lastPage={page.last}
                                firstPage={page.first}
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
