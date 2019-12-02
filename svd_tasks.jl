using LinearAlgebra

function apply_svd(x::Array{Float64,2})
    svd(x)
end

function get_svd_s(thesvd::SVD{Float64,Float64,Array{Float64,2}})
    thesvd.S
end

function get_svd_u(thesvd::SVD{Float64,Float64,Array{Float64,2}})
    thesvd.U
end

function get_svd_vt(thesvd::SVD{Float64,Float64,Array{Float64,2}})
    thesvd.Vt
end


