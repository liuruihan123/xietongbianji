import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { InvitationsVO, InvitationsForm, InvitationsQuery } from '@/api/system/invitations/types';

/**
 * 查询邀请管理列表
 * @param query
 * @returns {*}
 */

export const listInvitations = (query?: InvitationsQuery): AxiosPromise<InvitationsVO[]> => {
  return request({
    url: '/system/invitations/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询邀请管理详细
 * @param id
 */
export const getInvitations = (id: string | number): AxiosPromise<InvitationsVO> => {
  return request({
    url: '/system/invitations/' + id,
    method: 'get'
  });
};

/**
 * 新增邀请管理
 * @param data
 */
export const addInvitations = (data: InvitationsForm) => {
  return request({
    url: '/system/invitations',
    method: 'post',
    data: data
  });
};

/**
 * 修改邀请管理
 * @param data
 */
export const updateInvitations = (data: InvitationsForm) => {
  return request({
    url: '/system/invitations',
    method: 'put',
    data: data
  });
};

/**
 * 删除邀请管理
 * @param id
 */
export const delInvitations = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/invitations/' + id,
    method: 'delete'
  });
};
